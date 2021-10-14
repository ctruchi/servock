package io.servock

import mu.KotlinLogging
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.lens.Header
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

private val logger = KotlinLogging.logger {}

object App {
    operator fun invoke(routeMatcher: List<Route>) =
        LogRequestAndResponse(logger, true)
            .then(
                routes(
                    *routeMatcher.map {
                        it.request.toRequest() to { request: Request -> it.response buildResponseFor request }
                    }.toTypedArray()
                )
            )
}

private fun RequestMatcher.toRequest() = path bind method

private infix fun ResponseBuilder.buildResponseFor(request: Request) =
    with(this) {
        Response(status)
            .let {
                if (content != null) {
                    it.body(content.payload extrapolateWith request)
                        .with(Header.CONTENT_TYPE of content.contentType)
                } else {
                    it
                }
            }
    }

private infix fun String.extrapolateWith(request: Request) =
    "\\$\\{[^}]+}".toRegex().find(this)
        ?.let {
            it.groupValues
                .map {
                    it.substring(2, it.length - 1)
                }
                .fold(this) { res, s ->
                    res.replace("\${$s}", request.path(s) ?: "")
                }
        }
        ?: this
