plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.pjm_match"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pjm_match"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86")
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")

    val cameraxVersion = "1.4.1"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
    implementation("androidx.concurrent:concurrent-futures-ktx:1.2.0")

    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.mediapipe:tasks-vision:0.10.20")
    implementation("com.microsoft.onnxruntime:onnxruntime-android:1.22.0")
}

