import com.somanyfeeds.admin.Services
import com.somanyfeeds.cloudfoundry.readVcapServices
import com.somanyfeeds.cloudfoundry.services.mapPostgresDbConfig
import com.somanyfeeds.jetty.JettyApplication
import com.somanyfeeds.jetty.JettyManagedService
import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.util.resource.Resource
import java.util.*

class App : JettyApplication() {

    override val applicationServices = emptyList<JettyManagedService>()
    override val applicationHandlers: List<Handler>
    override val port: Int

    init {
        val vcapServices = readVcapServices()
        val dataSourceConfig = mapPostgresDbConfig(vcapServices)
        val services = Services(dataSourceConfig)

        val resourceHandler = ResourceHandler().apply {
            baseResource = Resource.newClassPathResource("static")
        }

        port = env("PORT").toInt()
        applicationHandlers = listOf(resourceHandler, services.feedsController)
    }


    private fun env(name: String) = System.getenv(name)
}

fun main(vararg args: String) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    App().start()
}
