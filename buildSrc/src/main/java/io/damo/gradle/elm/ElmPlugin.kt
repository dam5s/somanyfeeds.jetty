package io.damo.gradle.elm

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.Exec
import org.gradle.kotlin.dsl.task

class ElmPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val jsFile = "js/apps.js"
        val minifiedJsFile = jsFile.replace(".js", "-min.js")
        val appFiles = arrayOf("SoManyFeeds/App.elm")
        val htmlRunnerPath = "SoManyFeedsTests/HtmlRunner.elm"

        with(project) {
            val jsPath = "$buildDir/$jsFile"
            val minifiedJsPath = "$buildDir/$minifiedJsFile"
            val mainSrcDir = "$projectDir/src/main/elm"
            val testSrcDir = "$projectDir/src/test/elm"

            task<Exec>("install") {
                inputs.file("$mainSrcDir/elm-package.json")
                outputs.file("$mainSrcDir/elm-stuff/packages")

                workingDir(mainSrcDir)
                commandLine("elm-package", "install", "--yes")
            }

            task<Exec>("compile") {
                inputs.files(fileTree(mainSrcDir))
                outputs.file(file(jsPath))

                dependsOn("install")

                workingDir(mainSrcDir)
                commandLine("elm-make", "--yes", "--output", jsPath, *appFiles)
            }

            task("minify") {
                inputs.file(jsPath)
                outputs.file(minifiedJsPath)

                doLast {
                    JsMinifier().minify(jsPath, minifiedJsPath)
                }
            }

            // when doing dev, this task is useful to speed up click testing the elm apps
            task<Copy>("fakeMinify") {
                mustRunAfter("compile")
                from(jsPath)
                into(buildDir)
                rename(jsFile, minifiedJsFile)
            }

            task("build") {
                dependsOn("test", "compile", "minify")
            }

            task<Delete>("clean") {
                delete(
                    "build",
                    "$mainSrcDir/elm-stuff/build-artifacts",
                    "$testSrcDir/elm-stuff/build-artifacts"
                )
            }

            task<Exec>("testInstall") {
                inputs.file("$testSrcDir/elm-package.json")
                outputs.file("$testSrcDir/elm-stuff/packages")

                workingDir(testSrcDir)
                commandLine("elm-package", "install", "--yes")
            }

            task("test") {
                dependsOn("testInstall")

                inputs.file(fileTree(testSrcDir))
                inputs.file(fileTree(mainSrcDir))

                doLast {
                    val reactorProcess = ProcessBuilder()
                        .directory(file("$projectDir/src/test/elm"))
                        .command("elm-reactor")
                        .start()

                    val exitCode = ProcessBuilder()
                        .apply { environment().put("TZ", "America/Denver") }
                        .command("phantomjs", "$rootDir/buildSrc/src/main/js/phantomElmTestRunner.js", htmlRunnerPath)
                        .start()
                        .waitFor()

                    reactorProcess.destroyForcibly()

                    if (exitCode > 0) {
                        throw GradleException("Elm tests failed")
                    }
                }
            }
        }
    }
}
