package io.servock

import mu.KotlinLogging
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.lens.Header
import org.http4k.routing.bind
import org.http4k.routing.routes

private val logger = KotlinLogging.logger {}

object App {
    operator fun invoke(routeMatcher: List<Route>) =
        LogRequestAndResponse(logger, true)
            .then(
                routes(
                    *routeMatcher.map {
                        it.request.toRequest() to { _: Request -> it.response.toResponse() }
                    }.toTypedArray()
                )
            )
}

private fun RequestMatcher.toRequest() = path bind method

private fun ResponseBuilder.toResponse() =
    with(this) {
        Response(status)
            .let {
                if (content != null) {
                    it.body(content.payload)
                        .with(Header.CONTENT_TYPE of content.contentType)
                } else {
                    it
                }
            }
    }
