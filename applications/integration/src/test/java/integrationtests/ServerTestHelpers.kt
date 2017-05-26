package integrationtests

import com.somanyfeeds.restsupport.RestTemplate
import org.assertj.core.api.Assertions
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.util.resource.Resource
import java.time.Instant
import java.time.temporal.ChronoUnit

fun startApiServer(port: Int = 8081): Process {
    val workingDir = System.getProperty("user.dir")

    val process = ProcessBuilder()
        .command("java", "-jar", "$workingDir/../api/build/libs/api-all.jar", "server")
        .inheritIO()
        .env("VCAP_SERVICES", """{
              "postgres": [
                {
                  "label": "", "name": "", "tags": [],
                  "credentials": {
                    "uri": "postgres://dam5s@localhost:5432/somanyfeeds_integration"
                  }
                }
              ]
            }
            """)
        .env("PORT", port.toString())
        .env("TWITTER_CONSUMER_KEY", "<twitter_consumer_key>")
        .env("TWITTER_CONSUMER_SECRET", "<twitter_consumer_secret>")
        .env("TWITTER_ACCESS_TOKEN", "<twitter_access_token>")
        .env("TWITTER_ACCESS_TOKEN_SECRET", "<twitter_access_token_secret>")
        .start()

    waitUntilServerIsUp(port)
    return process
}

fun startFrontendServer(port :Int = 8082): Server {
    val workingDir = System.getProperty("user.dir")

    return Server(port).apply {
        stopAtShutdown = true
        handler = ResourceHandler().apply { baseResource = Resource.newResource("$workingDir/../frontend/build") }
        start()
    }
}

private fun ProcessBuilder.env(name: String, value: String) = apply { environment()[name] = value }

// timeout in seconds
private fun waitUntilServerIsUp(port: Int, timeout: Int = 10) {
    val restTemplate = RestTemplate()
    val start = Instant.now()
    var isUp = false

    while (!isUp) {
        try {
            restTemplate.get("http://localhost:$port")
            isUp = true
        } catch (e: Throwable) {
            val timeSpent = ChronoUnit.SECONDS.between(start, Instant.now())
            if (timeSpent > timeout) {
                Assertions.fail("Timed out waiting for server on port $port")
            }

            println("Waiting on port $port, ${e.javaClass}")
            Thread.sleep(200)
        }
    }
}
