import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    kotlin("plugin.serialization") version "1.4.32"
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "pl.ownvision.fastpocket"
        minSdk = 21
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"

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
    implementation("androidx.appcompat:appcompat:1.3.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    // Compose and AppCompat
    implementation("androidx.activity:activity-compose:1.3.0-beta01")
    implementation("androidx.compose.ui:ui:${rootProject.extra["composeVersion"]}")
    implementation("androidx.compose.material:material:${rootProject.extra["composeVersion"]}")
    implementation("androidx.compose.ui:ui-tooling:${rootProject.extra["composeVersion"]}")

    implementation("androidx.core:core-ktx:1.6.0-beta02")
    implementation("androidx.datastore:datastore:1.0.0-beta01")
    implementation("androidx.datastore:datastore-preferences:1.0.0-beta01")
    implementation("com.google.android.material:material:1.3.0")

    // Accompanist
    val accompanistVersion = "0.11.1"
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-insets:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-coil:$accompanistVersion")

    // Dagger / Hilt
    implementation("com.google.dagger:hilt-android:${rootProject.extra["hiltVersion"]}")
    kapt("com.google.dagger:hilt-android-compiler:${rootProject.extra["hiltVersion"]}")

    // Hilt binder
    val hiltBinderVersion = "1.1.0"
    implementation("com.paulrybitskyi:hilt-binder:$hiltBinderVersion")
    kapt("com.paulrybitskyi:hilt-binder-compiler:$hiltBinderVersion")

    // Lifecycle
    val lifecycleKtxVersion = "2.3.1"
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleKtxVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleKtxVersion")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    // Resilience
    implementation("com.michael-bull.kotlin-retry:kotlin-retry:1.0.8")

    // Mavericks
    implementation("com.airbnb.android:mavericks:2.3.0")
    implementation("com.airbnb.android:mavericks-compose:2.1.0-alpha02")

    // Logging
    implementation("com.jakewharton.timber:timber:4.7.1")

    // Image handling
    implementation("io.coil-kt:coil:1.2.1")

    // Networking
    api(platform("com.squareup.okhttp3:okhttp-bom:4.9.1"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:okhttp-brotli")

    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

    // Niddler (network traffic inspector inside Android Studio)
    val niddlerVersion = "1.5.4"
    debugImplementation("com.chimerapps.niddler:niddler:$niddlerVersion")
    releaseImplementation("com.chimerapps.niddler:niddler-noop:$niddlerVersion")

    // Json serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")

    // Coroutines
    val coroutinesVersion = "1.5.0"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    // Chrome web tabs
    implementation("androidx.browser:browser:1.3.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}