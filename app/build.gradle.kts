import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlinxSerializable)
}

android {
    namespace = "de.felixnuesse.disky"
    compileSdk = 36

    defaultConfig {
        applicationId = "de.felixnuesse.disky"
        minSdk = 30
        targetSdk = 36
        versionCode = 15
        versionName = "2.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file(project.findProperty("MYAPP_RELEASE_STORE_FILE") as String)
            storePassword = project.findProperty("MYAPP_RELEASE_STORE_PASSWORD") as String
            keyAlias = project.findProperty("MYAPP_RELEASE_KEY_ALIAS") as String
            keyPassword = project.findProperty("MYAPP_RELEASE_KEY_PASSWORD") as String
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    applicationVariants.all{
        outputs.all {
            if(name.contains("release"))
                (this as BaseVariantOutputImpl).outputFileName = "AndroidFileCleanup-release-v$versionName.apk"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    lint {
        disable += "MissingTranslation"
    }


    // https://gitlab.com/fdroid/fdroiddata/-/merge_requests/33304
    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }

}

dependencies {
    implementation("com.github.woheller69:FreeDroidWarn:V1.+")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.appintro)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.kotlinx.serialization.json)

    // graphics
    implementation(libs.lottie)

    // Caching
    implementation (libs.glide)

    // Logging
    implementation(libs.timber)
    implementation(libs.treessence)
}