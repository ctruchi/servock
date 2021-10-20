package io.servock

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import mu.KLogging
import org.http4k.core.ContentType
import org.http4k.core.Parameters
import org.http4k.core.Status
import java.nio.file.Paths
import kotlin.math.log

private val mapper = ObjectMapper()
    .registerModule(
        KotlinModule.Builder()
            .build()
    )

class Conf(val confPath: String) {

    init {
        logger.info("Reading conf from $confPath")
    }

    val routes = mapper.readerFor(RouteDto::class.java).readValues<RouteDto>(Paths.get(confPath).toFile())
        .asSequence()
        .toList()
        .map {
            Route(
                request = RequestMatcher(
                    path = it.request.path,
                    method = it.request.method
                ),
                response = ResponseBuilder(
                    status = Status(it.response.status, null),
                    content = it.response.content?.run {
                        Content(
                            payload = payload,
                            contentType = contentType.asContentType()
                        )
                    }
                )
            )
        }

    companion object: KLogging()
}

private data class RouteDto(
    val request: RequestMatcher,
    val response: ResponseBuilderDto
)

private data class ResponseBuilderDto(
    val status: Int,
    val content: ContentDto?
)

private data class ContentDto(
    val payload: String,
    val contentType: String
)

private fun parseValueAndDirectives(it: String): Pair<String, Parameters> =
    with(it.split(";").mapNotNull { it.trim().takeIf(String::isNotEmpty) }) {
        first() to drop(1).map {
            with(it.split("=")) {
                first() to if (size == 1) null else drop(1).joinToString("=")
            }
        }
    }

private fun String.asContentType() = parseValueAndDirectives(this).let {
    ContentType(it.first, it.second
        .filter { it.first.toLowerCase() in setOf("boundary", "charset", "media-type") }
    )
}
