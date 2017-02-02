import com.somanyfeeds.admin.Services
import com.somanyfeeds.cloudfoundry.readVcapServices
import com.somanyfeeds.cloudfoundry.services.mapPostgresDbConfig
import com.somanyfeeds.jetty.JettyApplication
import com.somanyfeeds.jetty.JettyManagedService
import org.eclipse.jetty.server.Handler
import java.util.*

class App : JettyApplication() {

    override val applicationServices = emptyList<JettyManagedService>()
    override val applicationHandlers: List<Handler>
    override val port: Int

    init {
        val vcapServices = readVcapServices()
        val dataSourceConfig = mapPostgresDbConfig(vcapServices)

        val services = Services(dataSourceConfig)

        port = env("PORT").toInt()
        applicationHandlers = listOf(services.feedsController)
    }


    private fun env(name: String) = System.getenv(name)
}

fun main(vararg args: String) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    App().start()
}
