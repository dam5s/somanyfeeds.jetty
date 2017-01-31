package com.somanyfeeds.jdbcsupport

import org.skife.jdbi.v2.DBI
import org.skife.jdbi.v2.Handle
import java.sql.ResultSet
import javax.sql.DataSource

class JdbcTemplate(dataSource: DataSource) {

    fun <T> query(sql: String, rowMapper: (ResultSet) -> T): List<T> {
        return withHandle { handle ->
            handle
                .createQuery(sql)
                .map { index, rs, ctxt -> rowMapper(rs) }
                .list()
        }
    }

    fun execute(vararg sqlStatements: String) = dbi.open().use {
        for (sql in sqlStatements) {
            it.execute(sql)
        }
    }


    private val dbi = DBI(dataSource)

    private fun <T> withHandle(block: (Handle) -> T): T = dbi.open().use(block)
}
