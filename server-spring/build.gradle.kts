plugins {
    java
    alias(libs.plugins.spring.boot)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation(platform(libs.spring.boot.bom))
    annotationProcessor(platform(libs.spring.boot.bom))

    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.websocket)
    implementation(libs.spring.boot.starter.flyway)
    implementation(libs.spring.boot.starter.data.jpa)

    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.flyway.database.postgresql)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}