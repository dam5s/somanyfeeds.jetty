package com.somanyfeeds.articlesdataaccess

import java.time.LocalDateTime

class ArticlesRepository {

    fun findAll() = listOf(
        Article(link = "http://example.com/1", content = "Hello World 1!", date = LocalDateTime.now()),
        Article(link = "http://example.com/2", content = "Hello World 2!", date = LocalDateTime.now()),
        Article(link = "http://example.com/3", content = "Hello World 3!", date = LocalDateTime.now())
    )
}
