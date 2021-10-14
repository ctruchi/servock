package io.servock

import org.http4k.core.ContentType.Companion.TEXT_PLAIN
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.header
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
            val app = App(
                listOf(
                    Route(
                        request = RequestMatcher(
                            path = "/test",
                            method = GET
                        ),
                        response = ResponseBuilder(
                            status = OK,
                            content = Content("content", TEXT_PLAIN)
                        )
                    )
                )
            )
            val response = app(Request(GET, "/test"))
            expectThat(response) {
                get { status }.isEqualTo(OK)
                get { bodyString() }.isEqualTo("content")
                get { header("Content-Type") }.isEqualTo("text/plain; charset=utf-8")
            }
        }

        @Test
        fun `should respond to request without body`() {
            val app = App(
                listOf(
                    Route(
                        request = RequestMatcher(
                            path = "/test",
                            method = GET
                        ),
                        response = ResponseBuilder(
                            status = OK
                        )
                    )
                )
            )
            val response = app(Request(GET, "/test"))
            expectThat(response) {
                get { status }.isEqualTo(OK)
                get { bodyString() }.isNullOrEmpty()
                get { header("Content-Type") }.isNull()
            }
        }

        @Test
        fun `should not respond`() {
            val app = App(
                listOf(
                    Route(
                        request = RequestMatcher(
                            path = "/test",
                            method = GET
                        ),
                        response = ResponseBuilder(
                            status = OK,
                            content = Content("content", TEXT_PLAIN)
                        )
                    )
                )
            )
            val response = app(Request(GET, "/wrongpath"))

            expectThat(response) {
                get { status }.isEqualTo(NOT_FOUND)
            }
        }
    }

    @Nested
    inner class Dynamic {

        @Test
        fun `should reppond with path variable`() {
            val app = App(
                listOf(
                    Route(
                        request = RequestMatcher(
                            path = "/test/{variable}",
                            method = GET
                        ),
                        response = ResponseBuilder(
                            status = OK,
                            content = Content("Hello \${variable}", TEXT_PLAIN)
                        )
                    )
                )
            )

            val response = app(Request(GET, "/test/foo"))

            expectThat(response) {
                get { status }.isEqualTo(OK)
                get { bodyString() }.isEqualTo("Hello foo")
                get { header("Content-Type") }.isEqualTo("text/plain; charset=utf-8")
            }
        }
    }

}
