/**
 * Configuration of all gradle build plugins
 */
object BuildPlugins {
    const val ANDROID_APPLICATION = "com.android.application"
    const val ANDROID_LIBRARY = "com.android.library"

    const val GOOGLE_SERVICES = "com.google.gms.google-services"

    const val SPRING_BOOT = "org.springframework.boot"
    const val SPRING_DM = "io.spring.dependency-management"

    const val KOTLIN_MULTIPLATFORM = "org.jetbrains.kotlin.multiplatform"
    const val KOTLIN_ANDROID = "org.jetbrains.kotlin.android"
    const val KOTLIN_JVM = "org.jetbrains.kotlin.jvm"
    const val KOTLIN_KAPT = "org.jetbrains.kotlin.kapt"

    const val KOTLIN_PLUGIN_SPRING = "org.jetbrains.kotlin.plugin.spring"
    const val KOTLIN_PLUGIN_SERIALIZATION = "org.jetbrains.kotlin.plugin.serialization"
    const val KOTLIN_PLUGIN_PARCELIZE = "org.jetbrains.kotlin.plugin.parcelize"

    const val SQL_DELIGHT = "com.squareup.sqldelight"
}