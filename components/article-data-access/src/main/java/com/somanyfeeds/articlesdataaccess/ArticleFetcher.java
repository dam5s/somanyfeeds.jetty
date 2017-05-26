package com.somanyfeeds.articlesdataaccess;

import java.time.LocalDateTime;

public class ArticleFetcher {

    public ArticleRecord fetch() {
        return new ArticleRecord(
            null,
            toString(),
            toString(),
            toString(),
            LocalDateTime.now(),
            toString()
        );
    }

}
