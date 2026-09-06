import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)

}

// VK ID app credentials, read from the gitignored local.properties rather than committed -
// create a VK ID app at https://id.vk.ru/business/go and add these four keys there:
//   VKIDClientID=<app_id>
//   VKIDClientSecret=<client_secret>
//   VKIDRedirectHost=vk.ru
//   VKIDRedirectScheme=vk<app_id>
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        FileInputStream(localPropertiesFile).use { load(it) }
    }
}

android {
    namespace = "me.alexy.hipipl"
    compileSdk = 37

    defaultConfig {
        applicationId = "me.alexy.hipipl"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "0.$versionCode"

        vectorDrawables {
            useSupportLibrary = true
        }

        addManifestPlaceholders(
            mapOf(
                "VKIDClientID" to (localProperties.getProperty("VKIDClientID") ?: ""),
                "VKIDClientSecret" to (localProperties.getProperty("VKIDClientSecret") ?: ""),
                "VKIDRedirectHost" to (localProperties.getProperty("VKIDRedirectHost") ?: "vk.ru"),
                "VKIDRedirectScheme" to (localProperties.getProperty("VKIDRedirectScheme") ?: "vk0"),
            )
        )
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
             .forEach { output ->
                val outputFileName = "$applicationId-${variant.buildType.name}-${variant.versionName}.apk"
                output.outputFileName = outputFileName
            }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
    }


    buildFeatures {
        compose = true
        aidl = false
        buildConfig = false
        renderScript = false
        shaders = false
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":feature:hostme"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:menu"))

    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Koin Dependency Injection
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Arch Components
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // Compose
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // VK ID (VKID.init() call in HiPeople.kt)
    implementation(libs.vkid)

    // Tooling
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
