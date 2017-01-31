package com.somanyfeeds.jdbcsupport

import kotlinx.support.jdk7.use
import org.skife.jdbi.v2.DBI
import org.skife.jdbi.v2.Handle
import java.sql.ResultSet
import javax.sql.DataSource

class JdbcTemplate(val dataSource: DataSource) {

    fun create(tableName: String, fields: Map<String, Any?>): Long {
        val columns = fields.keys
        val columnsSQL = columns.joinToString()
        val valuesSQL = columns.map { ":$it" }.joinToString()
        val createSQL = "INSERT INTO $tableName ($columnsSQL) VALUES ($valuesSQL)"

        return withHandle { handle ->
            handle
                .createStatement(createSQL)
                .bindFromMap(fields)
                .executeAndReturnGeneratedKeys({ index, rs, ctxt ->
                    rs.getLong(1)
                })
                .first()
        }
    }

    fun <T> query(sql: String, rowMapper: (ResultSet) -> T): List<T> {
        return withHandle { handle ->
            handle
                .createQuery(sql)
                .map { index, rs, ctxt -> rowMapper(rs) }
                .list()
        }
    }

    fun execute(sql: String, vararg args: Any) = withHandle { handle ->
        handle.execute(sql, *args)
    }

    fun count(sql: String) = dataSource.connection.use { connection ->
        connection
            .prepareStatement(sql)
            .executeQuery()
            .apply { next() }
            .getLong(1)
    }


    private val dbi = DBI(dataSource)

    private fun <T> withHandle(block: (Handle) -> T): T = dbi.open().use(block)
}
