plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.simpleaudioplayer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.simpleaudioplayer"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.wear.compose:compose-material:1.3.1")
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-session:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")
    implementation("org.jellyfin.media3:media3-ffmpeg-decoder:1.5.0+1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    //dependency for the navigation bar
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Auto-Dependency Injection Framework
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    // Fast & Easy?? Image loading for Jetpack Compose:
    implementation("com.github.bumptech.glide:glide:4.14.2") // class version?
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01") // compose version


    // Hilt View Model - For Android Compose?
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    // Hilt For navigation (passing viewmodels to diff screens)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Extra icons for Audio Player
    implementation("androidx.compose.material:material-icons-extended-android:1.7.6")

    // Permission Handler Library
    implementation("com.google.accompanist:accompanist-permissions:0.37.0")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}
