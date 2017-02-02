package com.somanyfeeds.feeddataaccess

import com.somanyfeeds.jdbcsupport.JdbcTemplate
import java.sql.ResultSet

class FeedRepository(val jdbcTemplate: JdbcTemplate) {

    fun findAll() = jdbcTemplate.query(findAllSQL) { rs -> mapper(rs) }

    fun find(id: Long) = jdbcTemplate.find(findSQL, id) { rs -> mapper(rs) }


    private val mapper = { rs: ResultSet ->
        FeedRecord(
            id = rs.getLong(1),
            name = rs.getString(2),
            slug = rs.getString(3),
            info = rs.getString(4),
            type = feedTypeFromString(rs.getString(5))
        )
    }

    private val findSQL = "SELECT id, name, slug, info, type FROM feed WHERE id = ?"

    private val findAllSQL = "SELECT id, name, slug, info, type FROM feed"
}
