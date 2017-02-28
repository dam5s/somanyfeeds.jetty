package integrationtests

import com.somanyfeeds.restsupport.RestResult
import com.somanyfeeds.restsupport.RestTemplate
import io.damo.aspen.Test
import org.assertj.core.api.Assertions.assertThat

class IntegrationTest : Test({

    var serverProcess: Process? = null

    before {
        serverProcess = startApiServer()
    }

    after {
        serverProcess?.destroy()
    }

    test {
        val restTemplate = RestTemplate()
        val response = restTemplate.get("http://localhost:8081/articles")

        assertThat(response is RestResult.Success).isTrue()
    }
})
