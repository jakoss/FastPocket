buildscript {
    @Suppress("UNUSED_VARIABLE") val composeVersion by extra("1.0.0-beta09")
    val hiltVersion by extra("2.37")
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0-alpha02")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.20")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
    }
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
        kotlinOptions.freeCompilerArgs =
            listOf("-Xjvm-default=compatibility", "-Xuse-experimental=kotlin.Experimental")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}