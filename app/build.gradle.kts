plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.sanjey.codestride"
    compileSdk = 35

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }


    defaultConfig {


        applicationId = "com.sanjey.codestride"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "OPENAI_API_KEY",
            "\"${project.findProperty("OPENAI_API_KEY") ?: ""}\""
        )
        buildConfigField(
            "String",
            "YOUTUBE_API_KEY",
            "\"${project.findProperty("YOUTUBE_API_KEY") ?: ""}\""
        )
    }

        buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
            debug {
                isMinifyEnabled = false
            }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

// Retrofit + Gson converter
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// OkHttp for interceptors (for Authorization header)
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    implementation("com.google.firebase:firebase-functions-ktx")

    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation ("androidx.work:work-runtime-ktx:2.10.3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation ("com.airbnb.android:lottie-compose:6.1.0")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3:1.2.0-beta01")
    implementation("com.google.dagger:hilt-android:2.48")
//    implementation(libs.firebase.functions.ktx)
    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-auth-ktx:22.1.2")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.ui:ui:1.6.7")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.7")
    implementation("com.google.firebase:firebase-firestore-ktx:24.9.1")
    implementation(libs.androidx.runtime.livedata)
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.7")
    implementation("androidx.compose.foundation:foundation:1.6.7")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}