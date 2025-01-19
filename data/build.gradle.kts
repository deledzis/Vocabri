plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.sqldelight)
}

android {
    namespace = "com.vocabri.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
    }
}

sqldelight {
    databases {
        create("VocabriDatabase") {
            packageName.set("com.vocabri.data")
            generateAsync.set(true)
        }
    }
}

dependencies {
    implementation(projects.domain)

    implementation(libs.kotlinx.coroutines)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    implementation(libs.androidx.core.ktx)

    implementation(libs.sqldelight.android)
    implementation(libs.sqldelight.coroutines)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.sqldelight.jvm)

    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.androidx.junit)
}