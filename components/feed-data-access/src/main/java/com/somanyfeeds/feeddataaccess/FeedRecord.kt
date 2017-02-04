package com.somanyfeeds.feeddataaccess

data class FeedRecord(
    val id: Long? = null,
    val name: String,
    val slug: String,
    val info: String,
    val type: FeedType
)

data class FeedUpdates(
    val name: String,
    val slug: String,
    val info: String,
    val type: FeedType
)

enum class FeedType { RSS, ATOM, TWITTER, CUSTOM }

fun feedTypeFromString(value: String) =
    when (value) {
        "RSS" -> FeedType.RSS
        "ATOM" -> FeedType.ATOM
        "TWITTER" -> FeedType.TWITTER
        "CUSTOM" -> FeedType.CUSTOM
        else -> throw IllegalArgumentException("Invalid string value for FeedType")
    }
