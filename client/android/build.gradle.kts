import dependencies.Dependencies
import dependencies.DependenciesVersions.JETPACK_COMPOSE_COMPILER

plugins {
    id(BuildPlugins.ANDROID_APPLICATION)
    id(BuildPlugins.KOTLIN_ANDROID)
    id(BuildPlugins.KOTLIN_PLUGIN_PARCELIZE)
    id(BuildPlugins.GOOGLE_SERVICES)
}

android {
    compileSdk = BuildAndroidConfig.COMPILE_SDK_VERSION

    defaultConfig {
        applicationId = BuildAndroidConfig.APPLICATION_ID

        minSdk = BuildAndroidConfig.MIN_SDK_VERSION
        targetSdk = BuildAndroidConfig.TARGET_SDK_VERSION

        versionCode = BuildAndroidConfig.VERSION_CODE
        versionName = BuildAndroidConfig.VERSION_NAME

        multiDexEnabled = true

        testInstrumentationRunner = BuildAndroidConfig.TEST_INSTRUMENTATION_RUNNER
    }

    signingConfigs {
        getByName(BuildType.DEBUG) {
            keyAlias = "debug"
            keyPassword = "12345678"
            storeFile = file("./src/debug/keystore")
            storePassword = "12345678"
        }
        create(BuildType.RELEASE) {
            keyAlias = "addeep"
            keyPassword = "addeep@2O22"
            storeFile = file("./src/release/keystore")
            storePassword = "addeep@2O22"
        }
    }

    buildTypes {
        getByName(BuildType.DEBUG) {
            isDebuggable = BuildTypeDebug.isDebuggable
            isMinifyEnabled = BuildTypeDebug.isMinifyEnabled
            signingConfig = signingConfigs.getByName(BuildType.DEBUG)
        }
        getByName(BuildType.RELEASE) {
            isDebuggable = BuildTypeRelease.isDebuggable
            isMinifyEnabled = BuildTypeRelease.isMinifyEnabled
            signingConfig = signingConfigs.getByName(BuildType.RELEASE)
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = JETPACK_COMPOSE_COMPILER
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
        freeCompilerArgs = freeCompilerArgs + arrayOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all",
//            "-P",
//            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true",
        )
    }

    packagingOptions {
        resources {
            excludes.add("META-INF/*.kotlin_module")
        }
    }

    sourceSets {
        getByName("main") {
            java.srcDir("src/main/kotlin")
        }
        getByName("test") {
            java.srcDir("src/test/kotlin")
        }
        getByName("androidTest") {
            java.srcDir("src/androidTest/kotlin")
        }
    }
}

dependencies {
    // Desugar
    coreLibraryDesugaring(Dependencies.Android.CORE_DESUGAR)
    // Static lib
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // Core module
    implementation(project(BuildModules.CLIENT_CORE))

    // Kotlinx
    implementation(Dependencies.Android.KOTLIN_COROUTINES_PLAY_SERVICES)

    // Koin
    implementation(Dependencies.Android.KOIN)
    implementation(Dependencies.Android.KOIN_COMPOSE)

    // Jetpack
    // Core
    implementation(Dependencies.Android.JETPACK_CORE)
    // Lifecycle
    implementation(Dependencies.Android.JETPACK_LIFECYCLE)
    implementation(Dependencies.Android.JETPACK_LIFECYCLE_VIEWMODEL)
    implementation(Dependencies.Android.JETPACK_LIFECYCLE_VIEWMODEL_COMPOSE)
    implementation(Dependencies.Android.JETPACK_LIFECYCLE_SERVICE)
    implementation(Dependencies.Android.JETPACK_LIFECYCLE_PROCESS)
    // Compose
    implementation(Dependencies.Android.JETPACK_COMPOSE_UI)
    implementation(Dependencies.Android.JETPACK_COMPOSE_UI_TOOLING)
    implementation(Dependencies.Android.JETPACK_COMPOSE_FOUNDATION)
    implementation(Dependencies.Android.JETPACK_COMPOSE_MATERIAL)
    implementation(Dependencies.Android.JETPACK_COMPOSE_MATERIAL_ICON)
    implementation(Dependencies.Android.JETPACK_COMPOSE_MATERIAL_ICON_EXTENDED)
    implementation(Dependencies.Android.JETPACK_COMPOSE_MATERIAL3)
    // Accompanist
    implementation(Dependencies.Android.ACCOMPANIST_PERMISSIONS)
    implementation(Dependencies.Android.ACCOMPANIST_INSETS)
    implementation(Dependencies.Android.ACCOMPANIST_PAGER)
    implementation(Dependencies.Android.ACCOMPANIST_PAGER_INDICATORS)
    // Navigtion
    implementation(Dependencies.Android.JETPACK_NAVIGATION_COMPOSE)
    // Activity
    implementation(Dependencies.Android.JETPACK_ACTIVITY)
    implementation(Dependencies.Android.JETPACK_ACTIVITY_COMPOSE)
    // Appcompat
    implementation(Dependencies.Android.JETPACK_APPCOMPAT)
    // Material
    implementation(Dependencies.Android.MATERIAL)

    // AppCenter
    implementation(Dependencies.Android.APPCENTER_ANALYTICS)
    implementation(Dependencies.Android.APPCENTER_CRASHES)

    // Coil
    implementation(Dependencies.Android.COIL)
    implementation(Dependencies.Android.COIL_COMPOSE)
    implementation(Dependencies.Android.COIL_GIF)
    implementation(Dependencies.Android.COIL_VIDEO)

    // Animated WebP
    implementation(Dependencies.Android.ANIMATED_WEBP)

    // Google services
    implementation(Dependencies.Android.FIREBASE_MESSAGING)
    implementation(Dependencies.Android.FIREBASE_ANALYTICS)
    implementation(Dependencies.Android.FIREBASE_AUTH)

    implementation(Dependencies.Android.UCROP)
}