# So Many Feeds

So Many Feeds is a feed aggregator.

Easily manage your feeds from different social websites,
and aggregate in one simple home page.

So Many Feeds will automatically generate for you
any combination of your feeds you like,
both as a web page and as a RSS feed.


## Development dependencies

 * Java 8
 * Elm 0.18
 * Postgres
 * PhantomJS
 * Graphviz (for the Dependencies Graph)

## Setup Databases

```bash
createdb somanyfeeds_dev
createdb somanyfeeds_integration
createdb somanyfeeds_jdbc_test
createdb somanyfeeds_feed_test
createdb somanyfeeds_article_test

psql somanyfeeds_dev < db/schema.ddl
psql somanyfeeds_integration < db/schema.ddl
psql somanyfeeds_jdbc_test < db/schema.ddl
psql somanyfeeds_feed_test < db/schema.ddl
psql somanyfeeds_article_test < db/schema.ddl
```

## Github OAuth

https://developer.github.com/apps/building-integrations/setting-up-and-registering-oauth-apps/about-authorization-options-for-oauth-apps/
