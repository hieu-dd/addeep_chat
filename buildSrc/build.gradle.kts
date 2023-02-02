plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenLocal()
    google()
    mavenCentral()
}

object PluginsVersions {
    const val KOTLIN = "1.6.10"
    const val GRADLE_SPRING_BOOT = "2.6.1"
    const val GRADLE_SPRING_DM = "1.0.11.RELEASE"
    const val GRADLE_ANDROID = "7.0.4"
    const val GOOGLE_SERVICES = "4.3.10"
    const val GRADLE_SQL_DELIGHT = "1.5.3"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${PluginsVersions.KOTLIN}")
    implementation("org.jetbrains.kotlin:kotlin-allopen:${PluginsVersions.KOTLIN}")
    implementation("org.jetbrains.kotlin:kotlin-serialization:${PluginsVersions.KOTLIN}")

    implementation("org.springframework.boot:spring-boot-gradle-plugin:${PluginsVersions.GRADLE_SPRING_BOOT}")
    implementation("io.spring.gradle:dependency-management-plugin:${PluginsVersions.GRADLE_SPRING_DM}")

    implementation("com.android.tools.build:gradle:${PluginsVersions.GRADLE_ANDROID}")
    implementation("com.google.gms:google-services:${PluginsVersions.GOOGLE_SERVICES}")

    implementation("com.squareup.sqldelight:gradle-plugin:${PluginsVersions.GRADLE_SQL_DELIGHT}")

    implementation("com.google.gms:google-services:${PluginsVersions.GOOGLE_SERVICES}")
}
