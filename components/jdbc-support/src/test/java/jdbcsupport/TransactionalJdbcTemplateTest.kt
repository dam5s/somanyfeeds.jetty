package jdbcsupport

import com.somanyfeeds.jdbcsupport.TransactionalJdbcTemplate
import io.damo.aspen.Test
import kotlinx.support.jdk7.use
import org.assertj.core.api.Assertions.assertThat

class TransactionalJdbcTemplateTest : Test({

    val dataSource = createTestDataSource("somanyfeeds_jdbc_test")
    val template = TransactionalJdbcTemplate(dataSource)
    val transactionManager = template.transactionManager

    before {
        dataSource.connection.use { connection ->
            connection.createStatement().apply {
                executeUpdate("DROP TABLE IF EXISTS person")

                executeUpdate("""
                  CREATE TABLE person (
                    id         BIGSERIAL PRIMARY KEY,
                    name       TEXT
                  )
                """)
            }
        }
    }

    test {
        transactionManager.withTransaction {
            template.create("person", mapOf("name" to "John"))
            template.create("person", mapOf("name" to "Jane"))
        }

        assertThat(template.count("SELECT count(*) FROM person")).isEqualTo(2)
    }

    test("on exception") {
        try {
            transactionManager.withTransaction {
                template.create("person", mapOf("name" to "John"))
                template.create("person", mapOf("name" to "Jane"))
                throw RuntimeException("Oops")
            }
        } catch (ignored: RuntimeException) {
        }

        assertThat(template.count("SELECT count(*) FROM person")).isEqualTo(0)
    }
})
