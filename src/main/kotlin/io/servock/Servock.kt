package io.servock

import mu.KLogger
import org.http4k.core.Filter
import org.http4k.core.HttpMessage
import org.http4k.core.MemoryBody
import org.http4k.core.then
import org.http4k.filter.RequestFilters
import org.http4k.server.Netty
import org.http4k.server.asServer


fun main() {
    App(emptyList())
        .asServer(Netty(System.getProperty("port", "8080").toInt()))
        .start()
}

object LogRequest {
    operator fun invoke(logger: KLogger, debugStream: Boolean) = RequestFilters.Tap {
        logger.debug("Request: {}: {} - {}", it.method, it.uri, it.printable(debugStream))
    }
}

object LogResponse {
    operator fun invoke(logger: KLogger, debugStream: Boolean) = Filter { next ->
        {
            try {
                next(it).also { response ->
                    logger.debug(
                        "Response: {}: {}: {} - {}",
                        it.method,
                        it.uri,
                        response.status.code,
                        response.printable(debugStream)
                    )
                }
            } catch (e: Exception) {
                logger.debug("Response failed: {}: {}", it.method, it.uri)
                throw e
            }
        }
    }
}

object LogRequestAndResponse {
    operator fun invoke(logger: KLogger, debugStream: Boolean = false) =
        LogRequest(logger, debugStream).then(LogResponse(logger, debugStream))
}

private fun HttpMessage.printable(debugStream: Boolean) =
    if (debugStream || body is MemoryBody) this else body("<<stream>>")
