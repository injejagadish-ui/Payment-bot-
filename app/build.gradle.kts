plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.smartpaymentalert"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.smartpaymentalert"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
}
