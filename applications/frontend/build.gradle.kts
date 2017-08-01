import io.damo.gradle.elm.ElmPlugin
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.util.resource.Resource

apply {
    plugin(ElmPlugin::class.java)
}

task<Copy>("copyAssets") {
    mustRunAfter("clean")
    from("$projectDir/src/main") {
        include("**/*.html")
        include("**/*.css")
        include("**/*.png")
        include("**/*.ico")
        include("**/*.svg")
        include("Staticfile")
        exclude("elm")
    }
    into(buildDir)
}

tasks.findByName("build").dependsOn("copyAssets")

task<Exec>("deploy") {
    dependsOn("build")
    workingDir(project.projectDir)
    commandLine("cf", "push", "damo", "-p", "build")
}

task("run") {
    dependsOn("build")

    doLast {
        Server(8000).apply {
            stopAtShutdown = true
            handler = ResourceHandler().apply { baseResource = Resource.newResource(project.buildDir) }
            start()
            join()
        }
    }
}
