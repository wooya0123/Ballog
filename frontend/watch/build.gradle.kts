import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.ballog.watch"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ballog.watch"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        create("release") {
            val props = Properties().apply {
                load(rootProject.file("local.properties").inputStream())
            }

            storeFile = file(props["KEYSTORE_PATH"] as String)
            storePassword = props["KEYSTORE_PASSWORD"] as String
            keyAlias = props["KEY_ALIAS"] as String
            keyPassword = props["KEY_PASSWORD"] as String
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    applicationVariants.all {
        outputs.all {
            val outputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            val appName = "ballog"
            val moduleName = "watch"
            val version = defaultConfig.versionName
            outputImpl.outputFileName = "$appName-$moduleName-v$version.apk"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.play.services.wearable)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.wear.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)

    // 추가 의존성
    implementation("androidx.wear.compose:compose-navigation:1.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
