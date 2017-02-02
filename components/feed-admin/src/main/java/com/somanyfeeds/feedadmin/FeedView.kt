package com.somanyfeeds.feedadmin

import com.somanyfeeds.feeddataaccess.FeedRecord

data class FeedView(
    val id: Long?,
    val name: String,
    val slug: String,
    val info: String,
    val type: String
)

fun present(record: FeedRecord) = FeedView(
    id = record.id,
    name = record.name,
    slug = record.slug,
    info = record.info,
    type = record.type.toString()
)
