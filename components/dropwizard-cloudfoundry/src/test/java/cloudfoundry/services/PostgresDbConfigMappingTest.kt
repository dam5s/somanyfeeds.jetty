package cloudfoundry.services

import io.damo.aspen.Test
import io.damo.dropwizard.cloudfoundry.VcapService
import io.damo.dropwizard.cloudfoundry.configs.DataSourceConfig
import io.damo.dropwizard.cloudfoundry.services.mapPostgresDbConfig
import org.assertj.core.api.Assertions

class PostgresDbConfigMappingTest : Test({
    describe("#mapPostgresDbConfig") {
        test {
            val config = mapPostgresDbConfig(listOf(
                buildVcapService(credentials = emptyMap()),
                buildVcapService(credentials = mapOf("uri" to "postgres://user1:pass1@foo.db.elephantsql.com:5432/database1"))
            ))

            Assertions.assertThat(config).isEqualTo(DataSourceConfig(
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

            Assertions.assertThat(config).isEqualTo(DataSourceConfig(
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

