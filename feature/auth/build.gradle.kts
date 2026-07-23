import java.io.FileInputStream
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // Remove when fixed https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        FileInputStream(localPropertiesFile).use { load(it) }
    }
}

android {
    namespace = "me.alexy.hipipl.feature.auth"
    compileSdk = 37

    defaultConfig {
        minSdk = 26

        // Must match the redirect_uri VK ID's SDK uses internally when requesting the auth code
        // (same VKIDRedirectScheme/VKIDRedirectHost keys as :app's manifest placeholders),
        // since POST /api/v1/login_vk validates it against what VK issued the code for.
        val vkidRedirectScheme = localProperties.getProperty("VKIDRedirectScheme") ?: "vk0"
        val vkidRedirectHost = localProperties.getProperty("VKIDRedirectHost") ?: "vk.ru"
        buildConfigField("String", "VKID_REDIRECT_URI", "\"$vkidRedirectScheme://$vkidRedirectHost/blank.html\"")
    }

    buildFeatures {
        compose = true
        aidl = false
        buildConfig = true
        renderScript = false
        shaders = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:presentation"))
    implementation(project(":core:designsystem"))

    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.androidx.activity.compose)

    // Koin Dependency Injection
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)

    // Arch Components
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // Compose
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // VK ID
    implementation(libs.vkid)
    implementation(libs.vkid.onetap.compose)

    // Tooling
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Local tests
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
