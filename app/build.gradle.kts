import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.pethospitalmanagement"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.pethospitalmanagement"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    viewBinding {
        enable = true
    }
}

dependencies {
    val room_version = "2.5.2"
    implementation( "org.jetbrains.kotlin:kotlin-stdlib:1.5.31") // Match this version with your Kotlin plugin version

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
// Room
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:2.5.0")
    implementation ("io.insert-koin:koin-android:3.4.3")
    implementation ("androidx.room:room-ktx:$room_version")

// ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")

// Coroutine extensions for ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("androidx.recyclerview:recyclerview:1.2.0")// Version may vary

    implementation ("com.google.android.material:material:1.4.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2") // หรือเวอร์ชันล่าสุด


}
