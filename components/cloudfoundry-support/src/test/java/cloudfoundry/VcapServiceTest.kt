package cloudfoundry

import com.somanyfeeds.cloudfoundry.VcapService
import com.somanyfeeds.cloudfoundry.readVcapServices
import io.damo.aspen.Test
import org.assertj.core.api.Assertions.assertThat

class VcapServiceTest : Test({
    test {
        val env = mapOf("VCAP_SERVICES" to """
          {
            "postgres": [
              {
                "label": "some-label",
                "name": "some-name",
                "tags": ["tag-1", "tag-2"],
                "credentials": {
                  "uri": "postgres://dam5s@localhost:5432/somanyfeeds_dev"
                }
              }
            ]
          }
        """)


        val services = readVcapServices(getenv = { env[it] })


        assertThat(services).isEqualTo(listOf(
            VcapService(
                credentials = mapOf("uri" to "postgres://dam5s@localhost:5432/somanyfeeds_dev"),
                label = "some-label",
                name = "some-name",
                tags = listOf("tag-1", "tag-2")
            )
        ))
    }
})
