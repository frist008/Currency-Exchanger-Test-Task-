plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.di.hilt)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.room)
    alias(libs.plugins.google.services)
}

android {
    compileSdk = libs.versions.sdk.compile.asProvider().get().toInt()
    compileSdkExtension = libs.versions.sdk.compile.extension.get().toInt()
    buildToolsVersion = libs.versions.build.tools.version.get()

    defaultConfig {
        applicationId = "ua.testtask.currencyexchanger"
        namespace = applicationId
        minSdk = libs.versions.sdk.min.get().toInt()
        targetSdk = libs.versions.sdk.target.get().toInt()
        versionCode = libs.versions.version.asProvider().get().toInt()
        versionName = libs.versions.version.name.get()

        // https://developer.android.com/guide/topics/resources/app-languages#gradle-config
        resourceConfigurations += listOf("en", "ru", "ua")

        vectorDrawables {
            useSupportLibrary = true
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String[]",
            "CERTIFICATE_ARR",
            "{${wrap("sha256/q1yIuzIjaZZgFypWeK+np1J1Bb1Rxa7cyqllwfX0w54=")}," +
                "${wrap("sha256/4a6cPehI7OG6cuDZka5NDZ7FR8a60d3auda+sKfg4Ng=")}," +
                "${wrap("sha256/x4QzPSC810K5/cMjb05Qm4k3Bw5zBn4lTdO/nEW/Td4=")}}",
        )
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true

            resValue("bool", "firebase_analytics_logcat_enabled", "true")

        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            resValue("bool", "firebase_analytics_logcat_enabled", "false")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        targetCompatibility(libs.versions.jvm.target.asProvider().get().toInt())
        sourceCompatibility(libs.versions.jvm.target.asProvider().get().toInt())
    }

    kotlinOptions {
        jvmTarget = libs.versions.jvm.target.kotlin.get()
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

private fun wrap(str: String) = "\"$str\""

tasks.withType(Test::class) {
    allOpen {
        annotation("androidx.annotation.OpenForTesting")
    }

    testLogging { setExceptionFormat("full") }
}

dependencies {
    // Kotlin
    implementation(platform(libs.kotlin.bom))
    implementation(libs.bundles.kotlin.bom)
    implementation(libs.bundles.kotlin.common)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.compose.tooling)

    // UI
    implementation(libs.bundles.common)
    implementation(libs.bundles.ui)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase.bom)

    // Framework
    implementation(libs.bundles.di)
    ksp(libs.bundles.database.compiler)
    implementation(libs.bundles.network)
    implementation(libs.bundles.database)
    ksp(libs.bundles.di.compiler)
    implementation(libs.bundles.multithreading)

    // Util
    coreLibraryDesugaring(libs.jdk.desugar)
    implementation(libs.leakcanary)
    debugImplementation(libs.leakcanary.debug)
    implementation(libs.timber)

    // test
    testImplementation(libs.bundles.test)
}