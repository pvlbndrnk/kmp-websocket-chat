import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

val properties = Properties()
val propertiesFile = file("build.properties")
if (propertiesFile.exists()) {
    propertiesFile.inputStream().use(properties::load)
}
val baseUrl = properties.getProperty("baseUrl")
    ?.trim()
    ?.takeIf { it.isNotEmpty() }
    ?: "http://10.0.2.2/"

android {
    namespace = "com.pvlbndrnk.chat.android"
    compileSdk = 37

    buildFeatures {
        resValues = true
    }

    defaultConfig {
        applicationId = "com.pvlbndrnk.chat"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("debug") {
            resValue("string", "server_base_url", baseUrl)
        }
        getByName("release") {
            resValue("string", "server_base_url", "https://pvlbndrnk.duckdns.org/")
        }
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
}
