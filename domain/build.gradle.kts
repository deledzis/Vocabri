plugins {
    alias(libs.plugins.kotlin)
}

dependencies {
    implementation(libs.kotlinx.coroutines)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}