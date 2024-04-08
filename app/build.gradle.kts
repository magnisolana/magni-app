plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlinx-serialization")
    id("dagger.hilt.android.plugin")
    id("com.google.android.gms.oss-licenses-plugin")
}

android {
    namespace = "com.example.magni"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.Magni"
        minSdk = 26
        targetSdk = 34
        versionCode = 12
        versionName = "v1.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations.addAll(listOf("en"))
    }

    signingConfigs {
        create("release") {
            val storePath = findProperty("apkSigningKeystorePath") as String?
            storeFile = storePath?.let { file(it) }
            storePassword = findProperty("apkSigningKeystorePassword") as String?
            keyAlias = findProperty("apkSigningKeyAlias") as String?
            keyPassword = findProperty("apkSigningKeyPassword") as String?
        }
    }

    buildFeatures {
        viewBinding = true
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isDebuggable = false
            isShrinkResources = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            val releaseSigningConfig = signingConfigs["release"]
            if (releaseSigningConfig.storeFile != null) {
                signingConfig = releaseSigningConfig
            }
        }
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_17)
        targetCompatibility(JavaVersion.VERSION_17)
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.material3.android)
    val cameraxVersion = "1.3.2"

    implementation(libs.androidx.core.ktx.v160)
    implementation(libs.androidx.appcompat.v131)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v113)
    androidTestImplementation(libs.androidx.espresso.core.v340)

    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    implementation("androidx.security:security-crypto:1.0.0")

    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:1.0.0-alpha24")

    implementation("com.journeyapps:zxing-android-embedded:4.1.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    implementation("com.github.metaplex-foundation.SolanaKT:solana:2.1.1")

    implementation(libs.hilt.android)
    implementation(libs.hilt.android.gradle.plugin)
    ksp(libs.hilt.compiler)
    ksp(libs.hilt.android)
    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

}
