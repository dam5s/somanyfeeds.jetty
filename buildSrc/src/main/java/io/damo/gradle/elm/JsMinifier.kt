package io.damo.gradle.elm

import com.google.javascript.jscomp.*
import com.google.javascript.jscomp.CompilerOptions.Environment.BROWSER
import java.io.File
import java.nio.charset.StandardCharsets.UTF_8

class JsMinifier {

    fun minify(inputPath: String, outputPath: String) {
        val compiler = Compiler()
        val inputSourceFile = SourceFile.fromPath(pathFromString(inputPath), UTF_8)
        val outputFile = File(outputPath)

        val result = compiler.compile(externs, listOf(inputSourceFile), options)

        when {
            result.success -> {
                outputFile.parentFile.mkdirs()
                outputFile.writeText(compiler.toSource())
            }

            else -> {
                val error = result.errors.joinToString(separator = "") {
                    "${it.sourceName}:${it.lineNumber} - ${it.description}\n"
                }
                throw RuntimeException(error)
            }
        }
    }


    private val externs = CommandLineRunner.getBuiltinExterns(BROWSER)

    private val options = CompilerOptions().apply {
        CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(this)
        WarningLevel.QUIET.setOptionsForWarningLevel(this)
    }

    private fun pathFromString(filePath: String) = File(filePath).toPath()
}
