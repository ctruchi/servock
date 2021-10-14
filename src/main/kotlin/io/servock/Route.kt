package io.servock

import org.http4k.core.ContentType
import org.http4k.core.Method
import org.http4k.core.Status

data class Route(
    val request: RequestMatcher,
    val response: ResponseBuilder
)

data class RequestMatcher(
    val path: String,
    val method: Method,
)

data class ResponseBuilder(
    val status: Status,
    val content: Content? = null
)

data class Content(
    val payload: String,
    val contentType: ContentType
)
