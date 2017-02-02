package com.somanyfeeds.jdbcsupport

import kotlinx.support.jdk7.use
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement.RETURN_GENERATED_KEYS
import java.time.LocalDate
import java.time.LocalDateTime
import javax.sql.DataSource

open class JdbcTemplate(internal val dataSource: DataSource) {

    fun create(tableName: String, fields: Map<String, Any?>): Long {
        val columns = fields.keys
        val columnsSQL = columns.joinToString()
        val valuesSQL = columns.map { "?" }.joinToString()
        val createSQL = "INSERT INTO $tableName ($columnsSQL) VALUES ($valuesSQL)"

        return withConnection { connection ->
            val statement = connection
                .prepareStatement(createSQL, RETURN_GENERATED_KEYS)
                .bind(fields.values)
                .apply { execute() }

            statement
                .generatedKeys
                .apply { next() }
                .getLong(1)
        }
    }

    fun <T> query(sql: String, vararg bindings: Any, rowMapper: (ResultSet) -> T) = withConnection { connection ->
        connection
            .prepareStatement(sql)
            .bind(bindings)
            .executeQuery()
            .map(rowMapper)
    }

    fun <T> find(sql: String, vararg bindings: Any, rowMapper: (ResultSet) -> T) = withConnection { connection ->
        connection
            .prepareStatement(sql)
            .bind(bindings)
            .executeQuery()
            .mapFirst(rowMapper)
    }

    fun execute(sql: String, vararg bindings: Any) = withConnection { connection ->
        connection
            .prepareStatement(sql)
            .bind(bindings)
            .execute()
    }

    fun count(sql: String) = withConnection { connection ->
        connection
            .prepareStatement(sql)
            .executeQuery()
            .apply { next() }
            .getLong(1)
    }


    internal open fun <T> withConnection(function: (Connection) -> T)
        = dataSource.connection.use { function(it) }

    private fun <T> ResultSet.map(mapping: (ResultSet) -> T): List<T> {
        val results = arrayListOf<T>()

        while (this.next()) {
            results.add(mapping(this))
        }

        return results
    }

    private fun <T> ResultSet.mapFirst(mapping: (ResultSet) -> T): T {
        next()
        return mapping(this)
    }

    private fun PreparedStatement.bind(bindings: Collection<Any?>): PreparedStatement {
        bindings.forEachIndexed { index, value ->
            val bindingIndex = index + 1

            when (value) {
                is String -> setString(bindingIndex, value)
                is Int -> setInt(bindingIndex, value)
                is Long -> setLong(bindingIndex, value)
                is LocalDate -> setDate(bindingIndex, java.sql.Date.valueOf(value))
                is LocalDateTime -> setTimestamp(bindingIndex, java.sql.Timestamp.valueOf(value))
                null -> setString(bindingIndex, null)
                else -> throw IllegalArgumentException("Unsupported binding for value of type ${value.javaClass}")
            }
        }
        return this
    }

    private fun PreparedStatement.bind(bindings: Array<out Any?>) = bind(bindings.toList())
}
