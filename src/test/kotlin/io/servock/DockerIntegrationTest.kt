package io.servock

import org.http4k.client.JavaHttpClient
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Test
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.BindMode.READ_ONLY
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.function.Consumer

class DockerIntegrationTest {

    private val client = JavaHttpClient()

    @Test
    fun `should get response from servock container`() {
        val container = KGenericContainer("ctruchi/servock").apply {
            withExposedPorts(8080)
            setWaitStrategy(Wait.forListeningPort())
            withClasspathResourceMapping("docker-routes.json", "/conf/routes.json", READ_ONLY)
            withEnv("JAVA_OPTS", "-Dconf=/conf/routes.json")
            withLogConsumer(Consumer { print(it.utf8String) })
            start()
        }

        val response = client(Request(GET, "http://localhost:${container.firstMappedPort}/test"))

        expectThat(response) {
            get { status }.isEqualTo(OK)
        }
    }
}

class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)
