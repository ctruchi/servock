package io.servock

import org.http4k.core.ContentType
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isNull
import strikt.assertions.withFirst
import kotlin.io.path.toPath

internal class ConfTest {

    @Test
    fun `should load route without body`() {
        val routes = Conf("route-without-body.json".resourceAsPath()).routes

        expectThat(routes)
            .hasSize(1)
            .withFirst {
                with("request", { request }) {
                    get { path }.isEqualTo("/test")
                    get { method }.isEqualTo(GET)
                }
                with("response", { response }) {
                    get { status }.isEqualTo(OK)
                    get { content }.isNull()
                }
            }
    }

    @Test
    fun `should load route with body`() {
        val routes = Conf("route-with-body.json".resourceAsPath()).routes

        expectThat(routes)
            .hasSize(1)
            .withFirst {
                with("request", { request }) {
                    get { path }.isEqualTo("/test")
                    get { method }.isEqualTo(GET)
                }
                with("response", { response }) {
                    get { status }.isEqualTo(OK)
                    get { content }.isNotNull()
                        .and {
                            get { payload }.isEqualTo("test content")
                            get { contentType }.isEqualTo(ContentType.TEXT_PLAIN)

                        }
                }
            }
    }

    private fun String.resourceAsPath() =
        Thread.currentThread().contextClassLoader.getResource(this).toURI().toPath().toString()
}
