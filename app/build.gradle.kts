plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.pjm_match"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.pjm_match"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }
}
