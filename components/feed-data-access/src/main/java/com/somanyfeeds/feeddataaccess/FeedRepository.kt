package com.somanyfeeds.feeddataaccess

import com.somanyfeeds.jdbcsupport.JdbcTemplate

class FeedRepository(val jdbcTemplate: JdbcTemplate) {

    fun findAll() = jdbcTemplate.query(findAllSQL) { rs ->
        FeedRecord(
            id = rs.getLong(1),
            name = rs.getString(2),
            slug = rs.getString(3),
            info = rs.getString(4),
            type = feedTypeFromString(rs.getString(5))
        )
    }

    private val findAllSQL = "SELECT id, name, slug, info, type FROM feed"
}
