buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-beta02")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.35.1")
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