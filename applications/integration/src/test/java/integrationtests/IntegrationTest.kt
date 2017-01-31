package integrationtests

import com.somanyfeeds.restsupport.RestResult
import com.somanyfeeds.restsupport.RestTemplate
import io.damo.aspen.Test
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import java.time.Instant
import java.time.temporal.ChronoUnit

class IntegrationTest : Test({

    val restTemplate = RestTemplate()

    fun ProcessBuilder.env(name: String, value: String) = apply { environment()[name] = value }

    // timeout in seconds
    fun waitUntilServerIsUp(port: Int, timeout: Int = 10) {
        val start = Instant.now()
        var isUp = false

        while (!isUp) {
            try {
                restTemplate.get("http://localhost:$port")
                isUp = true
            } catch (e: Throwable) {
                val timeSpent = ChronoUnit.SECONDS.between(start, Instant.now())
                if (timeSpent > timeout) {
                    fail("Timed out waiting for server on port $port")
                }

                println("Waiting on port $port, ${e.javaClass}")
                Thread.sleep(200)
            }
        }
    }

    var serverProcess: Process? = null

    before {
        val workingDir = System.getProperty("user.dir")

        serverProcess = ProcessBuilder()
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
            .start()

        waitUntilServerIsUp(8080)
    }

    after {
        serverProcess?.destroy()
    }

    test {
        val response = restTemplate.get("http://localhost:8080/articles")

        assertThat(response is RestResult.Success).isTrue()
    }
})
