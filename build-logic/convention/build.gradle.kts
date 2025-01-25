plugins {
    `kotlin-dsl`
}

group = "com.vocabri.build_logic.convention"

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.detekt.gradle.plugin)
    compileOnly(libs.kover.gradle.plugin)
    compileOnly(libs.spotless.gradle.plugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("project") {
            id = "com.vocabri.project"
            implementationClass = "ProjectConventionPlugin"
        }
        register("android-application") {
            id = "com.vocabri.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("android-library") {
            id = "com.vocabri.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("kover") {
            id = "com.vocabri.kover"
            implementationClass = "KoverConventionPlugin"
        }
    }
}
