package com.somanyfeeds.jdbcsupport

interface TransactionManager {
    fun <T> withTransaction(function: () -> T): T
}
