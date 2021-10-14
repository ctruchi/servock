package io.servock

import org.http4k.core.ContentType
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNull
import strikt.assertions.isNullOrEmpty

internal class AppTest {

    @Nested
    inner class Static {

        @Test
        fun `should respond to request with body`() {
            testResponse("/test", GET, OK, "content" to ContentType.TEXT_PLAIN)
        }

        @Test
        fun `should respond to request without body`() {
            testResponse("/test", GET, OK)
        }
    }

    private fun testResponse(
        path: String,
        method: Method,
        status: Status,
        content: Pair<String, ContentType>? = null,
    ) {
        val app = App(
            listOf(
                Route(
                    request = RequestMatcher(
                        path = path,
                        method = method
                    ),
                    response = ResponseBuilder(
                        status = status,
                        content = content?.let { (payload, contentType) ->
                            Content(payload, contentType)
                        })
                )
            )
        )

        val response = app(Request(method, path))

        expectThat(response) {
            get { status }.isEqualTo(status)
            if (content != null) {
                get { bodyString() }.isEqualTo(content.first)
                get { header("Content-Type") }.isEqualTo(content.second.toHeaderValue())
            } else {
                get { bodyString() }.isNullOrEmpty()
                get { header("Content-Type") }.isNull()
            }
        }
    }
}
