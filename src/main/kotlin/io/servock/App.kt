package io.servock

import mu.KotlinLogging
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.then
import org.http4k.routing.bind
import org.http4k.routing.routes

private val logger = KotlinLogging.logger {}

object App {
    operator fun invoke() = LogRequestAndResponse(logger, true)
        .then(
            routes(
                "/" bind Method.GET to { _: Request -> Response(Status.OK) }
            )
        )
}
