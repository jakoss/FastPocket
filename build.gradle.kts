buildscript {
    @Suppress("UNUSED_VARIABLE") val composeVersion by extra("1.2.0")
    val hiltVersion by extra("2.43")
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
    }
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
        kotlinOptions.freeCompilerArgs =
            listOf("-Xjvm-default=compatibility", "-opt-in=kotlin.RequiresOptIn")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}