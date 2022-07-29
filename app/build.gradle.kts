import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version "1.7.0"
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "pl.ownvision.fastpocket"
        minSdk = 21
        targetSdk = 32
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = gradleLocalProperties(project.rootDir)
        val consumerKey =
            requireNotNull(properties.getProperty("consumer_key")) { "Missing consumer_key in local.properties" }
        val redirectUrl =
            requireNotNull(properties.getProperty("redirect_url")) { "Missing redirect_url in local.properties" }
        buildConfigField("String", "CONSUMER_KEY", "\"$consumerKey\"")
        buildConfigField("String", "REDIRECT_URL", "\"$redirectUrl\"")
    }

    signingConfigs {
        create("localApkSigning") {
            val properties = gradleLocalProperties(project.rootDir)
            storeFile = File(properties.getProperty("storeFilePath"))
            storePassword = properties.getProperty("storePassword")
            keyPassword = properties.getProperty("keyPassword")
            keyAlias = properties.getProperty("keyAlias")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("localApkSigning")
        }
        debug {
            signingConfig = signingConfigs.getByName("localApkSigning")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = rootProject.extra["composeVersion"] as String
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.4.2")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.6")

    // Compose and AppCompat
    implementation("androidx.activity:activity-compose:1.5.1")
    implementation("androidx.compose.ui:ui:${rootProject.extra["composeVersion"]}")
    implementation("androidx.compose.material:material:${rootProject.extra["composeVersion"]}")
    implementation("androidx.compose.material:material-icons-extended:${rootProject.extra["composeVersion"]}")
    implementation("androidx.compose.ui:ui-tooling:${rootProject.extra["composeVersion"]}")

    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.navigation:navigation-compose:2.5.0")

    // Accompanist
    val accompanistVersion = "0.25.0"
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-swiperefresh:$accompanistVersion")

    // Dagger / Hilt
    implementation("com.google.dagger:hilt-android:${rootProject.extra["hiltVersion"]}")
    kapt("com.google.dagger:hilt-android-compiler:${rootProject.extra["hiltVersion"]}")

    // Hilt binder
    val hiltBinderVersion = "1.1.2"
    implementation("com.paulrybitskyi:hilt-binder:$hiltBinderVersion")
    kapt("com.paulrybitskyi:hilt-binder-compiler:$hiltBinderVersion")

    // Lifecycle
    val lifecycleKtxVersion = "2.5.1"
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleKtxVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleKtxVersion")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    // Resilience
    implementation("com.michael-bull.kotlin-retry:kotlin-retry:1.0.8")

    // Mavericks
    implementation("com.airbnb.android:mavericks:2.7.0")
    implementation("com.airbnb.android:mavericks-compose:2.7.0")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Image handling
    val coilVersion = "2.1.0"
    implementation("io.coil-kt:coil:$coilVersion")
    implementation("io.coil-kt:coil-compose:$coilVersion")

    // Networking
    api(platform("com.squareup.okhttp3:okhttp-bom:4.10.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:okhttp-brotli")

    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

    // Niddler (network traffic inspector inside Android Studio)
    val niddlerVersion = "1.7.0"
    debugImplementation("com.chimerapps.niddler:niddler:$niddlerVersion")
    releaseImplementation("com.chimerapps.niddler:niddler-noop:$niddlerVersion")

    // Json serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    // Coroutines
    val coroutinesVersion = "1.6.4"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    // Chrome web tabs
    implementation("androidx.browser:browser:1.4.0")
}