package com.somanyfeeds.jdbcsupport

import kotlinx.support.jdk7.use
import java.sql.Connection
import javax.sql.DataSource

internal class InternalTransactionManager(val dataSource: DataSource) : TransactionManager {

    override fun <T> withTransaction(function: () -> T): T {
        allocateConnection().use { connection ->
            connection.autoCommit = false

            try {
                val result = function()
                connection.commit()
                connection.autoCommit = true
                return result

            } catch (t: Throwable) {
                connection.rollback()
                throw t
            } finally {
                releaseConnection()
            }
        }
    }

    internal fun getConnection() = localConnection.get()


    private val localConnection = ThreadLocal<Connection?>()

    private fun allocateConnection(): Connection {
        if (localConnection.get() != null) {
            throw IllegalStateException("Tried to start a transaction when one is already in progress")
        }

        val connection = dataSource.connection
        localConnection.set(connection)
        return connection
    }

    private fun releaseConnection() = localConnection.set(null)
}

