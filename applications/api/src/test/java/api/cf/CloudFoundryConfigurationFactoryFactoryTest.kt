package api.cf

import com.somanyfeeds.api.cf.CloudFoundryConfigurationFactoryFactory
import com.somanyfeeds.api.cf.configs.VcapService
import io.damo.aspen.Test
import org.assertj.core.api.Assertions.assertThat

class CloudFoundryConfigurationFactoryFactoryTest : Test({
    test("#create") {
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
        val factory = CloudFoundryConfigurationFactoryFactory<List<VcapService>>({ it }, getenv = { env[it] })


        val services = factory.create().build()


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
