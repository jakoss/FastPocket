import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    kotlin("plugin.serialization") version "1.5.0"
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
        val consumerKey = requireNotNull(properties.getProperty("consumer_key")) { "Missing consumer_key in local.properties" }
        val redirectUrl = requireNotNull(properties.getProperty("redirect_url")) { "Missing redirect_url in local.properties" }
        buildConfigField("String", "CONSUMER_KEY", "\"$consumerKey\"")
        buildConfigField("String", "REDIRECT_URL", "\"$redirectUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    implementation("androidx.core:core-ktx:1.5.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.datastore:datastore:1.0.0-beta01")
    implementation("androidx.datastore:datastore-preferences:1.0.0-beta01")

    // Dagger / Hilt
    val hiltVersion = "2.35.1"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")

    // Hilt binder
    val hiltBinderVersion = "1.0.0"
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
    val niddlerVersion = "1.5.3"
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