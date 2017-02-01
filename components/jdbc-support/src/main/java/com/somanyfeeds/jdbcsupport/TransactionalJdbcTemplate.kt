package com.somanyfeeds.jdbcsupport

import java.sql.Connection
import javax.sql.DataSource

class TransactionalJdbcTemplate(dataSource: DataSource) : JdbcTemplate(dataSource) {

    private val internalTransactionManager = InternalTransactionManager(dataSource)

    val transactionManager: TransactionManager
        get() = internalTransactionManager

    override fun <T> withConnection(function: (Connection) -> T): T {
        val connection = internalTransactionManager.getConnection()

        if (connection != null) {
            return function(connection)
        }

        return super.withConnection(function)
    }
}
