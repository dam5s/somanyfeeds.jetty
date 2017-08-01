package integrationtests

import io.damo.aspen.Test
import net.codestory.simplelenium.FluentTest
import org.eclipse.jetty.server.Server
import org.openqa.selenium.By.linkText

class AcceptanceTest : Test({

    var apiServerProcess: Process? = null
    var frontendServer: Server? = null

    before {
        apiServerProcess = startApiServer()
        frontendServer = startFrontendServer()
    }

    after {
        frontendServer?.stop()
        apiServerProcess?.destroy()
    }

    test {
        FluentTest("http://localhost:8082")
            .goTo("/")
            .find("svg").should().contain("damo.io")
            .find(linkText("Code")).click()
            .find("article h1").should().contain("dam5s")
    }
})
