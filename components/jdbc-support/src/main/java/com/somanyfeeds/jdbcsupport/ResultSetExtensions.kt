package com.somanyfeeds.jdbcsupport

import java.sql.ResultSet


fun ResultSet.getLocalDate(index: Int) = getDate(index).toLocalDate()

fun ResultSet.getLocalDateTime(index: Int) = getTimestamp(index).toLocalDateTime()
