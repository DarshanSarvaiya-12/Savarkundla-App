// ============================================================================
// WHY WE USE THIS SECTION: GRADLE PLUGINS
// Tells Gradle to build an Android App using Kotlin and Jetpack Compose UI.
// ============================================================================
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// ============================================================================
// WHY WE USE THIS SECTION: ANDROID APP SETTINGS
// Sets your unique app ID (in.savarkundla.app), minimum supported Android
// version (Android 7.0+), and Java 17 compatibility for Codemagic.
// ============================================================================
android {
    namespace = "in.savarkundla.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "in.savarkundla.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

// ============================================================================
// WHY WE USE THIS SECTION: EXTERNAL LIBRARIES (DEPENDENCIES)
// Downloads Material 3 UI components, extended icons (WhatsApp/Filter/Star),
// and Coil (coil-compose) so Shop Cards can load shop photos from the web.
// ============================================================================
dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.10.00")
    implementation(composeBom)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("io.coil-kt:coil-compose:2.7.0")
}
