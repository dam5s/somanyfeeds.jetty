import org.gradle.kotlin.dsl.*

plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
}

dependencies {
    compile(gradleKotlinDsl())
    compile(kotlin("stdlib"))
    compile("org.jetbrains.kotlinx:kotlinx-support-jdk8:0.3")

    compile("com.google.javascript:closure-compiler:v20170910")
}
