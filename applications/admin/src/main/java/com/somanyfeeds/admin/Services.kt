package com.somanyfeeds.admin

import com.somanyfeeds.cloudfoundry.configs.DataSourceConfig
import com.somanyfeeds.datasource.createDataSource
import com.somanyfeeds.feedadmin.FeedsController
import com.somanyfeeds.feeddataaccess.FeedRepository
import com.somanyfeeds.jdbcsupport.TransactionalJdbcTemplate
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler.RETHROW_HANDLER

class Services(dataSourceConfig: DataSourceConfig) {
    val dataSource = createDataSource(dataSourceConfig)
    val jdbcTemplate = TransactionalJdbcTemplate(dataSource)
    val transactionManager = jdbcTemplate.transactionManager

    val freemarker = Configuration(Configuration.VERSION_2_3_23).apply {
        setClassLoaderForTemplateLoading(javaClass.classLoader, "templates/")
        defaultEncoding = "UTF-8"
        templateExceptionHandler = RETHROW_HANDLER
        logTemplateExceptions = false
    }

    val feedsRepo = FeedRepository(jdbcTemplate)
    val feedsController = FeedsController(freemarker, feedsRepo)
    val mainController = MainController()
}
