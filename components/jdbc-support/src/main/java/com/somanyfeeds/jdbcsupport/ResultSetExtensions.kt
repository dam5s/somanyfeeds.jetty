package com.somanyfeeds.jdbcsupport

import java.sql.ResultSet
import java.time.LocalDate
import java.time.LocalDateTime


fun ResultSet.getLocalDate(index: Int): LocalDate
    = getDate(index).toLocalDate()

fun ResultSet.getLocalDateTime(index: Int): LocalDateTime
    = getTimestamp(index).toLocalDateTime()
