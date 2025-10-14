import java.util.Properties

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.hilt.gradle) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.vkid.manifest) apply true
}

vkidManifestPlaceholders {
    fun error() = logger.error(
        "Warning! Build will not work!\nCreate the 'secrets.properties' file in the 'sample/app' folder and add your 'VKIDClientID' and 'VKIDClientSecret' to it." +
                "\nFor more information, refer to the 'README.md' file."
    )

    val properties = Properties()
    properties.load(file("local.properties").inputStream())
    val clientId = properties["vk_app_id"] ?: error()
    val clientSecret = properties["vk_client_secret"] ?: error()
    init(
        clientId = clientId.toString(),
        clientSecret = clientSecret.toString(),
    )
}