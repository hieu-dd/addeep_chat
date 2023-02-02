package dependencies

/**
 * Configuration version of all dependencies
 */

object DependenciesVersions {
    const val KOTLIN = "1.6.10"
    const val KOTLIN_COROUTINES = "1.6.0"
    const val KOTLIN_SERIALIZATION = "1.3.2"
    const val KOTLIN_DATE_TIME = "0.3.1"

    const val OKIO = "3.0.0"

    const val KTOR = "1.6.7"
    const val SQL_DELIGHT = "1.5.3"
    const val STATELY = "1.2.1"
    const val KERMIT = "1.0.0"

    const val KOIN = "3.1.4" /*"3.1.5"*/

    const val CORE_DESUGAR = "1.1.5"
    const val JETPACK_CORE = "1.7.0"
    const val JETPACK_VIEWMODEL = "2.4.0"
    const val JETPACK_COMPOSE_COMPILER = "1.1.0-rc02"
    const val JETPACK_COMPOSE = "1.1.0-rc01"
    const val JETPACK_COMPOSE_MATERIAL3 = "1.0.0-alpha02"
    const val ACCOMPANIST = "0.22.0-rc"
    const val JETPACK_NAVIGATION = "2.4.0-rc01"
    const val JETPACK_ACTIVITY = "1.4.0"
    const val JETPACK_APPCOMPAT = "1.4.0"
    const val MATERIAL = "1.5.0-rc01"
    const val JWT = "3.18.2"
    const val APPCENTER = "4.3.1"
    const val RSOCKET = "0.14.3"
    const val SPRING_OAUTH_RESOURCE_SERVER = "5.6.0"
    const val SPRING_CLOUD_GCP_STARTER = "1.2.8.RELEASE"
    const val PHONE_NUMBER = "8.12.39"
    const val COIL = "2.0.0-alpha06"
    const val ANIMATED_WEBP = "2.17.0"
    const val FIREBASE_MESSAGING = "23.0.0"
    const val FIREBASE_ANALYTICS = "20.0.2"
    const val FIREBASE_AUTH = "21.0.1"
    const val FIREBASE_ADMIN = "8.1.0"
    const val UCROP = "2.2.7"
    const val APACHE_TIKA = "2.2.1"
}

/**
 * Project dependencies, makes it easy to include external binaries or
 * other library modules to build.
 */
object Dependencies {
    object Server {
        // Kotlin + Kotlinx
        const val KOTLIN =
            "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${DependenciesVersions.KOTLIN}"
        const val KOTLIN_REFLECT =
            "org.jetbrains.kotlin:kotlin-reflect:${DependenciesVersions.KOTLIN}"
        const val KOTLIN_COROUTINES =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${DependenciesVersions.KOTLIN_COROUTINES}"
        const val KOTLIN_COROUTINES_REACTOR =
            "org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${DependenciesVersions.KOTLIN_COROUTINES}"
        const val KOTLIN_COROUTINES_JDK8 =
            "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${DependenciesVersions.KOTLIN_COROUTINES}"

        // Reactor Kotlin
        const val REACTOR_KOTLIN =
            "io.projectreactor.kotlin:reactor-kotlin-extensions"

        // Spring
        const val SPRING_WEBFLUX =
            "org.springframework.boot:spring-boot-starter-webflux"
        const val SPRING_RSOCKET =
            "org.springframework.boot:spring-boot-starter-rsocket"
        const val SPRING_DATA_R2DBC =
            "org.springframework.boot:spring-boot-starter-data-r2dbc"
        const val SPRING_DATA_JDBC =
            "org.springframework.boot:spring-boot-starter-data-jdbc"
        const val LIQUIBASE =
            "org.liquibase:liquibase-core"
        const val SPRING_DATA_REDIS_REACTIVE =
            "org.springframework.boot:spring-boot-starter-data-redis-reactive"
        const val SPRING_SECURITY =
            "org.springframework.boot:spring-boot-starter-security"
        const val SPRING_SECURITY_RSOCKET =
            "org.springframework.security:spring-security-rsocket"
        const val SPRING_OAUTH_RESOURCE_SERVER =
            "org.springframework.security:spring-security-oauth2-resource-server:${DependenciesVersions.SPRING_OAUTH_RESOURCE_SERVER}"
        const val SPRING_CLOUD_SLEUTH =
            "org.springframework.cloud:spring-cloud-starter-sleuth"
        const val SPRING_CLOUD_GCP_STARTER =
            "org.springframework.cloud:spring-cloud-gcp-starter-storage:${DependenciesVersions.SPRING_CLOUD_GCP_STARTER}"
        const val PHONE_NUMBER =
            "com.googlecode.libphonenumber:libphonenumber:${DependenciesVersions.PHONE_NUMBER}"
        const val JWT =
            "com.auth0:java-jwt:${DependenciesVersions.JWT}"
        const val KOTLIN_SERIALIZATION_JSON =
            "org.jetbrains.kotlinx:kotlinx-serialization-json:${DependenciesVersions.KOTLIN_SERIALIZATION}"
        const val FIREBASE_ADMIN =
            "com.google.firebase:firebase-admin:${DependenciesVersions.FIREBASE_ADMIN}"
        const val APACHE_TIKA  =
            "org.apache.tika:tika-core:${DependenciesVersions.APACHE_TIKA}"
    }

    object Core {
        // Kotlinx
        const val KOTLIN_COROUTINES =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${DependenciesVersions.KOTLIN_COROUTINES}"
        const val KOTLIN_SERIALIZATION_JSON =
            "org.jetbrains.kotlinx:kotlinx-serialization-json:${DependenciesVersions.KOTLIN_SERIALIZATION}"
        const val KOTLIN_DATE_TIME =
            "org.jetbrains.kotlinx:kotlinx-datetime:${DependenciesVersions.KOTLIN_DATE_TIME}"

        // OKIO
        const val OKIO =
            "com.squareup.okio:okio:${DependenciesVersions.OKIO}"

        // Ktor
        const val KTOR_CLIENT =
            "io.ktor:ktor-client-core:${DependenciesVersions.KTOR}"
        const val KTOR_CLIENT_JSON =
            "io.ktor:ktor-client-json:${DependenciesVersions.KTOR}"
        const val KTOR_CLIENT_LOGGING =
            "io.ktor:ktor-client-logging:${DependenciesVersions.KTOR}"
        const val KTOR_CLIENT_SERIALIZATION =
            "io.ktor:ktor-client-serialization:${DependenciesVersions.KTOR}"
        const val KTOR_CLIENT_AUTH =
            "io.ktor:ktor-client-auth:${DependenciesVersions.KTOR}"
        const val KTOR_CLIENT_OKHTTP =
            "io.ktor:ktor-client-okhttp:${DependenciesVersions.KTOR}"
        const val KTOR_CLIENT_IOS =
            "io.ktor:ktor-client-ios:${DependenciesVersions.KTOR}"

        // SQL Delight
        const val SQL_DELIGHT =
            "com.squareup.sqldelight:runtime:${DependenciesVersions.SQL_DELIGHT}"
        const val SQL_DELIGHT_ANDROID =
            "com.squareup.sqldelight:android-driver:${DependenciesVersions.SQL_DELIGHT}"
        const val SQL_DELIGHT_IOS =
            "com.squareup.sqldelight:native-driver:${DependenciesVersions.SQL_DELIGHT}"
        const val SQL_DELIGHT_COROUTINE =
            "com.squareup.sqldelight:coroutines-extensions:${DependenciesVersions.SQL_DELIGHT}"

        // Stately
        const val STATELY_COMMON =
            "co.touchlab:stately-common:${DependenciesVersions.STATELY}"
        const val STATELY_CONCURRENCY =
            "co.touchlab:stately-concurrency:${DependenciesVersions.STATELY}"

        // Kermit
        const val KERMIT =
            "co.touchlab:kermit:${DependenciesVersions.KERMIT}"

        // Koin
        const val KOIN =
            "io.insert-koin:koin-core:${DependenciesVersions.KOIN}"

        // rSocket
        const val RSOCKET =
            "io.rsocket.kotlin:rsocket-core:${DependenciesVersions.RSOCKET}"
        const val RSOCKET_KTOR =
            "io.rsocket.kotlin:rsocket-transport-ktor-client:${DependenciesVersions.RSOCKET}"
    }

    object Android {
        // Kotlinx
        const val KOTLIN_COROUTINES_PLAY_SERVICES =
            "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${DependenciesVersions.KOTLIN_COROUTINES}"

        // Desugar
        const val CORE_DESUGAR =
            "com.android.tools:desugar_jdk_libs:${DependenciesVersions.CORE_DESUGAR}"

        // Koin
        const val KOIN =
            "io.insert-koin:koin-android:${DependenciesVersions.KOIN}"
        const val KOIN_COMPOSE =
            "io.insert-koin:koin-androidx-compose:${DependenciesVersions.KOIN}"

        // Jetpack
        const val JETPACK_CORE =
            "androidx.core:core-ktx:${DependenciesVersions.JETPACK_CORE}"
        const val JETPACK_LIFECYCLE =
            "androidx.lifecycle:lifecycle-runtime-ktx:${DependenciesVersions.JETPACK_VIEWMODEL}"
        const val JETPACK_LIFECYCLE_VIEWMODEL =
            "androidx.lifecycle:lifecycle-viewmodel-ktx:${DependenciesVersions.JETPACK_VIEWMODEL}"
        const val JETPACK_LIFECYCLE_VIEWMODEL_COMPOSE =
            "androidx.lifecycle:lifecycle-viewmodel-compose:${DependenciesVersions.JETPACK_VIEWMODEL}"
        const val JETPACK_LIFECYCLE_SERVICE =
            "androidx.lifecycle:lifecycle-service:${DependenciesVersions.JETPACK_VIEWMODEL}"
        const val JETPACK_LIFECYCLE_PROCESS =
            "androidx.lifecycle:lifecycle-process:${DependenciesVersions.JETPACK_VIEWMODEL}"
        const val JETPACK_COMPOSE_UI =
            "androidx.compose.ui:ui:${DependenciesVersions.JETPACK_COMPOSE}"
        const val JETPACK_COMPOSE_UI_TOOLING =
            "androidx.compose.ui:ui-tooling:${DependenciesVersions.JETPACK_COMPOSE}"
        const val JETPACK_COMPOSE_FOUNDATION =
            "androidx.compose.foundation:foundation:${DependenciesVersions.JETPACK_COMPOSE}"
        const val JETPACK_COMPOSE_MATERIAL =
            "androidx.compose.material:material:${DependenciesVersions.JETPACK_COMPOSE}"
        const val JETPACK_COMPOSE_MATERIAL_ICON =
            "androidx.compose.material:material-icons-core:${DependenciesVersions.JETPACK_COMPOSE}"
        const val JETPACK_COMPOSE_MATERIAL_ICON_EXTENDED =
            "androidx.compose.material:material-icons-extended:${DependenciesVersions.JETPACK_COMPOSE}"
        const val JETPACK_COMPOSE_MATERIAL3 =
            "androidx.compose.material3:material3:${DependenciesVersions.JETPACK_COMPOSE_MATERIAL3}"
        const val ACCOMPANIST_PERMISSIONS =
            "com.google.accompanist:accompanist-permissions:${DependenciesVersions.ACCOMPANIST}"
        const val ACCOMPANIST_INSETS =
            "com.google.accompanist:accompanist-insets:${DependenciesVersions.ACCOMPANIST}"
        const val ACCOMPANIST_PAGER =
            "com.google.accompanist:accompanist-pager:${DependenciesVersions.ACCOMPANIST}"
        const val ACCOMPANIST_PAGER_INDICATORS =
            "com.google.accompanist:accompanist-pager-indicators:${DependenciesVersions.ACCOMPANIST}"
        const val JETPACK_NAVIGATION_COMPOSE =
            "androidx.navigation:navigation-compose:${DependenciesVersions.JETPACK_NAVIGATION}"
        const val JETPACK_ACTIVITY =
            "androidx.activity:activity-ktx:${DependenciesVersions.JETPACK_ACTIVITY}"
        const val JETPACK_ACTIVITY_COMPOSE =
            "androidx.activity:activity-compose:${DependenciesVersions.JETPACK_ACTIVITY}"
        const val JETPACK_APPCOMPAT =
            "androidx.appcompat:appcompat:${DependenciesVersions.JETPACK_APPCOMPAT}"
        const val MATERIAL =
            "com.google.android.material:material:${DependenciesVersions.MATERIAL}"

        const val APPCENTER_ANALYTICS =
            "com.microsoft.appcenter:appcenter-analytics:${DependenciesVersions.APPCENTER}"
        const val APPCENTER_CRASHES =
            "com.microsoft.appcenter:appcenter-crashes:${DependenciesVersions.APPCENTER}"

        const val COIL =
            "io.coil-kt:coil:${DependenciesVersions.COIL}"
        const val COIL_COMPOSE =
            "io.coil-kt:coil-compose:${DependenciesVersions.COIL}"
        const val COIL_GIF =
            "io.coil-kt:coil-gif:${DependenciesVersions.COIL}"
        const val COIL_VIDEO =
            "io.coil-kt:coil-video:${DependenciesVersions.COIL}"
        const val ANIMATED_WEBP =
            "com.github.penfeizhou.android.animation:awebp:${DependenciesVersions.ANIMATED_WEBP}"

        const val FIREBASE_MESSAGING =
            "com.google.firebase:firebase-messaging-ktx:${DependenciesVersions.FIREBASE_MESSAGING}"
        const val FIREBASE_ANALYTICS =
            "com.google.firebase:firebase-analytics-ktx:${DependenciesVersions.FIREBASE_ANALYTICS}"
        const val FIREBASE_AUTH =
            "com.google.firebase:firebase-auth-ktx:${DependenciesVersions.FIREBASE_AUTH}"

        const val UCROP =
            "com.github.yalantis:ucrop:${DependenciesVersions.UCROP}"
    }

}