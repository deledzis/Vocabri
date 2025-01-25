import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("com.vocabri.android.library")
    alias(libs.plugins.sqldelight)
    id("com.vocabri.kover")
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
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.testLogging {
                    events = setOf(
                        TestLogEvent.FAILED,
                    )
                    exceptionFormat = TestExceptionFormat.FULL
                }
            }
        }
    }
}

sqldelight {
    databases {
        create("VocabriDatabase") {
            packageName.set("com.vocabri.data.db")
            generateAsync.set(true)
            schemaOutputDirectory.set(file("src/main/sqldelight/schema"))
            migrationOutputDirectory.set(file("src/main/sqldelight/migrations"))
        }
    }
}

dependencies {
    implementation(projects.domain)
    implementation(projects.core.logger)
    implementation(projects.core.utils)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.kermit.log)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    implementation(libs.androidx.core.ktx)

    implementation(libs.sqldelight.android)
    implementation(libs.sqldelight.coroutines)
    implementation(libs.sqldelight.jvm)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)

    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.androidx.junit)
}
