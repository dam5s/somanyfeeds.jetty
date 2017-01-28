package api.cf

import com.somanyfeeds.api.cf.configs.DataSourceConfig
import com.somanyfeeds.api.cf.configs.VcapService
import com.somanyfeeds.api.cf.configs.mapPostgresDbConfig
import io.damo.aspen.Test
import org.assertj.core.api.Assertions.assertThat

class CloudConfigTest : Test({

    describe("#postgresConfigFromUri") {
        test {
            val config = mapPostgresDbConfig(listOf(
                buildVcapService(credentials = emptyMap()),
                buildVcapService(credentials = mapOf("uri" to "postgres://user1:pass1@foo.db.elephantsql.com:5432/database1"))
            ))

            assertThat(config).isEqualTo(DataSourceConfig(
                serverName = "foo.db.elephantsql.com",
                databaseName = "database1",
                portNumber = 5432,
                user = "user1",
                password = "pass1"
            ))
        }

        test("without password") {
            val config = mapPostgresDbConfig(listOf(
                buildVcapService(credentials = mapOf("uri" to "postgres://user1@foo.db.elephantsql.com:5432/database1"))
            ))

            assertThat(config).isEqualTo(DataSourceConfig(
                serverName = "foo.db.elephantsql.com",
                databaseName = "database1",
                portNumber = 5432,
                user = "user1",
                password = null
            ))
        }
    }
})

fun buildVcapService(
    credentials: Map<String, Any> = emptyMap(),
    label: String = "my-service",
    name: String = "my-service",
    tags: List<String> = emptyList()
) = VcapService(credentials, label, name, tags)
