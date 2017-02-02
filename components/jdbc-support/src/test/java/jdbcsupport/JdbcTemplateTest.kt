package jdbcsupport

import com.somanyfeeds.cloudfoundry.configs.DataSourceConfig
import com.somanyfeeds.datasource.createDataSource
import com.somanyfeeds.jdbcsupport.JdbcTemplate
import com.somanyfeeds.jdbcsupport.getLocalDate
import com.somanyfeeds.jdbcsupport.getLocalDateTime
import io.damo.aspen.Test
import kotlinx.support.jdk7.use
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDate
import java.time.LocalDateTime

class JdbcTemplateTest : Test({

    val dataSource = createTestDataSource("somanyfeeds_jdbc_test")
    val template = JdbcTemplate(dataSource)


    before {
        dataSource.connection.use { connection ->
            connection.createStatement().apply {
                executeUpdate("DROP TABLE IF EXISTS person")

                executeUpdate("""
                  CREATE TABLE person (
                    id         BIGSERIAL PRIMARY KEY,
                    name       TEXT,
                    age        INT,
                    birth_date DATE,
                    updated_at TIMESTAMP
                  )
                """)
            }
        }
    }

    test("#create") {
        val id = template.create("person", mapOf(
            "name" to "John Doe",
            "age" to 35,
            "birth_date" to LocalDate.parse("1980-12-31"),
            "updated_at" to LocalDateTime.parse("2017-02-01T13:43:59")
        ))

        dataSource.connection.use { connection ->
            val rs = connection
                .createStatement()
                .executeQuery("SELECT id, name, age, birth_date, updated_at FROM person WHERE id = $id")
                .apply { next() }

            assertThat(rs.getLong(1)).isEqualTo(id)
            assertThat(rs.getString(2)).isEqualTo("John Doe")
            assertThat(rs.getInt(3)).isEqualTo(35)
            assertThat(rs.getDate(4)).isEqualTo(java.sql.Date.valueOf("1980-12-31"))
            assertThat(rs.getTimestamp(5)).isEqualTo(java.sql.Timestamp.valueOf("2017-02-01 13:43:59"))
        }
    }

    test("#create with null binding") {
        val id = template.create("person", mapOf(
            "name" to null,
            "age" to 35
        ))

        dataSource.connection.use { connection ->
            val rs = connection
                .createStatement()
                .executeQuery("SELECT name, age FROM person WHERE id = $id")
                .apply { next() }

            assertThat(rs.getString(1)).isNull()
            assertThat(rs.getInt(2)).isEqualTo(35)
        }
    }

    test("#query") {
        dataSource.connection.use { connection ->
            connection
                .createStatement()
                .executeUpdate("""
                    INSERT INTO person (id, name, age, birth_date, updated_at) VALUES
                      (10, 'Johnny', 10, '2006-04-02', '2016-02-01 14:00:10'),
                      (11, 'Liz', 12, '2004-03-02', '2016-02-01 14:01:11'),
                      (12, 'Brandon', 17, '2000-08-22', '2016-02-01 14:02:12')
                """)
        }

        data class Person(val id: Long, val name: String, val age: Int, val birthDate: LocalDate, val updatedAt: LocalDateTime)


        val people = template.query("SELECT id, name, age, birth_date, updated_at FROM person") { rs ->
            Person(
                id = rs.getLong(1),
                name = rs.getString(2),
                age = rs.getInt(3),
                birthDate = rs.getLocalDate(4),
                updatedAt = rs.getLocalDateTime(5)
            )
        }


        assertThat(people).containsExactly(
            Person(id = 10, name = "Johnny", age = 10, birthDate = LocalDate.parse("2006-04-02"), updatedAt = LocalDateTime.parse("2016-02-01T14:00:10")),
            Person(id = 11, name = "Liz", age = 12, birthDate = LocalDate.parse("2004-03-02"), updatedAt = LocalDateTime.parse("2016-02-01T14:01:11")),
            Person(id = 12, name = "Brandon", age = 17, birthDate = LocalDate.parse("2000-08-22"), updatedAt = LocalDateTime.parse("2016-02-01T14:02:12"))
        )
    }

    test("#count") {
        assertThat(template.count("SELECT count(*) FROM person")).isEqualTo(0)

        dataSource.connection.use { connection ->
            connection
                .createStatement()
                .executeUpdate("""
                    INSERT INTO person (name, age, birth_date, updated_at) VALUES
                      ('Johnny', 10, '2006-04-02', now()),
                      ('Liz', 12, '2004-03-02', now()),
                      ('Brandon', 17, '2000-08-22', now())
                """)
        }

        assertThat(template.count("SELECT count(*) FROM person")).isEqualTo(3)
    }
})

fun createTestDataSource(name: String) = createDataSource(DataSourceConfig(
    serverName = "localhost",
    databaseName = name,
    portNumber = 5432,
    user = "dam5s"
))
