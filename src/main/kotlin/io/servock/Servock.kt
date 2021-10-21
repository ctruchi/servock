package io.servock

import mu.KLogger
import mu.KotlinLogging
import org.http4k.core.Filter
import org.http4k.core.HttpMessage
import org.http4k.core.MemoryBody
import org.http4k.core.then
import org.http4k.filter.RequestFilters
import org.http4k.server.Netty
import org.http4k.server.asServer
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.Executors

private val logger = KotlinLogging.logger {}

private val executor = Executors.newSingleThreadExecutor()

fun main() {
    val confPath = Paths.get(System.getProperty("conf", "servock-routes.json"))
    if (confPath.toFile().isFile) {
        var server = buildServer(confPath)
        executor.submit({
            confPath.watch {
                server.stop()
                server = buildServer(it)
                server.start()
            }
        })
        server.start()
    } else {
        logger.error("$confPath should be a file")
    }
}

private fun buildServer(confPath: Path) =
    with(Conf(confPath)) {
        App(routes)
            .asServer(Netty(System.getProperty("port", "8080").toInt()))
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
