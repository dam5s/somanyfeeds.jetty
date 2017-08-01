buildscript {
    repositories {
        gradleScriptKotlin()
        jcenter()
    }

    dependencies {
        classpath(kotlinModule("gradle-plugin", "1.1.3-2"))
    }
}

apply {
    plugin("kotlin")
}

repositories {
    gradleScriptKotlin()
    jcenter()
}

dependencies {
    compile(gradleScriptKotlinApi())
    compile(kotlinModule("stdlib"))

    compile("com.yahoo.platform.yui:yuicompressor:2.4.8")
}
