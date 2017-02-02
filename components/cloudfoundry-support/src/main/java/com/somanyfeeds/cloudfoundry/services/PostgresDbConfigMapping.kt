package com.somanyfeeds.cloudfoundry.services

import com.somanyfeeds.cloudfoundry.VcapService
import com.somanyfeeds.cloudfoundry.configs.DataSourceConfig
import java.net.URI

fun mapPostgresDbConfig(services: List<VcapService>): DataSourceConfig {
    val service = postgresService(services)
    val uri = URI.create(service.credentials["uri"] as String)

    return postgresConfigFromUri(uri)
}

private fun postgresConfigFromUri(uri: URI): DataSourceConfig {
    val userInfo = uri.userInfo.split(":")
    val user = userInfo[0]
    val password = if (userInfo.size > 1) userInfo[1] else null

    return DataSourceConfig(
        serverName = uri.host,
        databaseName = uri.path.removePrefix("/"),
        portNumber = uri.port,
        user = user,
        password = password
    )
}

private fun postgresService(services: List<VcapService>): VcapService {
    return services.find {
        val uri = it.credentials["uri"] as? String
        uri?.contains("postgres") ?: false
    }
        ?: throw IllegalStateException("Could not find configuration for postgres")
}
