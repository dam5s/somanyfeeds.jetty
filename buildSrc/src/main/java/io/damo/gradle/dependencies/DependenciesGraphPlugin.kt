package io.damo.gradle.dependencies

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.UnknownConfigurationException
import org.gradle.api.tasks.Exec
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.task

class DependenciesGraphPlugin : Plugin<Project> {

    override fun apply(project: Project): Unit = with(project) {
        task("dependenciesGraphDot") {
            group = "DependenciesGraph"
            description = "Generate DOT file"

            mustRunAfter("clean")

            doLast {
                val graphBuildDir = "$buildDir/dependenciesGraph"

                delete(graphBuildDir)
                mkdir(graphBuildDir)

                var dotFileText = ""
                dotFileText += "digraph dependencies {\n"

                subprojects.forEach { subProject ->
                    try {
                        val compileConfig = subProject.configurations["compile"]
                        compileConfig
                            .dependencies
                            .filter { it is ProjectDependency }
                            .forEach {
                                val projectDependency = it as ProjectDependency
                                dotFileText += "  \"${subProject.name}\" -> \"${projectDependency.dependencyProject.name}\"\n"
                            }
                    } catch (ignored: UnknownConfigurationException) {
                    }
                }

                dotFileText += "}\n"
                file("$graphBuildDir/graph.dot").writeText(dotFileText)
            }
        }

        task<Exec>("dependenciesGraph") {
            group = "DependenciesGraph"
            description = "Generate PNG file"

            dependsOn("dependenciesGraphDot")
            workingDir("$buildDir/dependenciesGraph")
            commandLine("dot", "-O", "-Tpng", "graph.dot")
        }
    }
}
