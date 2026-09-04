plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.nada.kasir"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nada.kasir"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0-phase1"
    }

    // Product flavors = titik kustomisasi per pelanggan (poin 21).
    // Tambahkan flavor baru untuk tiap pelanggan tanpa mengubah kode inti.
    flavorDimensions += "client"
    productFlavors {
        create("demo") {
            dimension = "client"
            applicationIdSuffix = ".demo"
            resValue("string", "app_name", "NADA KASIR CUSTOM")
        }
        // create("tokoMakmur") {
        //     dimension = "client"
        //     applicationIdSuffix = ".tokomakmur"
        //     resValue("string", "app_name", "KASIR TOKO MAKMUR")
        // }
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")

    // Room (database lokal - wajib offline, poin 16 & 19)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Hilt (DI)
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // CameraX + ML Kit (barcode - Phase 2)
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    // Excel - (REMOVED) Apache POI is not suitable for Android; processing moved server-side
    // implementation("org.apache.poi:poi-ooxml:5.2.5")

    // Image loading
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Logging: replace Log4j with Timber for Android
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
}
