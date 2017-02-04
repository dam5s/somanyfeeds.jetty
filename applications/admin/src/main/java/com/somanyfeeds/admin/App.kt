import com.somanyfeeds.admin.Services
import com.somanyfeeds.cloudfoundry.readVcapServices
import com.somanyfeeds.cloudfoundry.services.mapPostgresDbConfig
import com.somanyfeeds.jetty.JettyApplication
import com.somanyfeeds.jetty.JettyControllerHandler
import java.util.*

class App(port: Int) : JettyApplication(port) {

    override fun configure(): JettyAppConfig {

        val vcapServices = readVcapServices()
        val dataSourceConfig = mapPostgresDbConfig(vcapServices)
        val services = Services(dataSourceConfig)

        return JettyAppConfig(
            services = emptyList(),
            handlers = listOf(
                buildStaticResourcesHandler(),
                buildJdbcSessionHandler(services.dataSource),
                JettyControllerHandler(services.feedsController)
            )
        )
    }
}

fun main(vararg args: String) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    App(System.getenv("PORT").toInt()).start()
}
