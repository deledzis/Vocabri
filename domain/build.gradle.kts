import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    alias(libs.plugins.kotlin)
    id("com.vocabri.kover")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    testLogging {
        events = setOf(
            TestLogEvent.FAILED,
        )
        exceptionFormat = TestExceptionFormat.FULL
    }
}

dependencies {
    implementation(projects.core.logger)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.kermit.log)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}
