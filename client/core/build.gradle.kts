import dependencies.Dependencies

plugins {
    id(BuildPlugins.ANDROID_LIBRARY)
    id(BuildPlugins.KOTLIN_MULTIPLATFORM)
    id(BuildPlugins.SQL_DELIGHT)
    id(BuildPlugins.KOTLIN_PLUGIN_SERIALIZATION)
}

android {
    compileSdk = BuildAndroidConfig.COMPILE_SDK_VERSION

    defaultConfig {
        minSdk = BuildAndroidConfig.MIN_SDK_VERSION
        targetSdk = BuildAndroidConfig.TARGET_SDK_VERSION
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
}

kotlin {
    android {
        compilations.all {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
        }

        publishLibraryVariants("release")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Dependencies.Core.KOTLIN_COROUTINES)
                api(Dependencies.Core.KOTLIN_SERIALIZATION_JSON)
                api(Dependencies.Core.KOTLIN_DATE_TIME)

                api(Dependencies.Core.OKIO)

                implementation(Dependencies.Core.KOIN)

                implementation(Dependencies.Core.SQL_DELIGHT)
                implementation(Dependencies.Core.SQL_DELIGHT_COROUTINE)

                implementation(Dependencies.Core.KTOR_CLIENT)
                implementation(Dependencies.Core.KTOR_CLIENT_JSON)
                implementation(Dependencies.Core.KTOR_CLIENT_LOGGING)
                implementation(Dependencies.Core.KTOR_CLIENT_SERIALIZATION)
                implementation(Dependencies.Core.KTOR_CLIENT_AUTH)

                implementation(Dependencies.Core.STATELY_COMMON)
                implementation(Dependencies.Core.STATELY_CONCURRENCY)

                api(Dependencies.Core.KERMIT)

                implementation(Dependencies.Core.RSOCKET)
                implementation(Dependencies.Core.RSOCKET_KTOR)
            }
        }
        val commonTest by getting {
            dependencies {
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Dependencies.Core.SQL_DELIGHT_ANDROID)
                implementation(Dependencies.Core.KTOR_CLIENT_OKHTTP)
            }
        }
        val androidTest by getting {
            dependencies {
            }
        }
    }
}

sqldelight {
    database("Addeep") {
        packageName = "net.itanchi.addeep.core.db"
    }
}