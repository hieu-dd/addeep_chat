import dependencies.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(BuildPlugins.SPRING_BOOT)
    id(BuildPlugins.SPRING_DM)
    id(BuildPlugins.KOTLIN_JVM)
    id(BuildPlugins.KOTLIN_PLUGIN_SPRING)
    id(BuildPlugins.KOTLIN_PLUGIN_SERIALIZATION)
    id(BuildPlugins.KOTLIN_KAPT)
}

dependencyManagement {
    imports {
        mavenBom ("org.springframework.cloud:spring-cloud-dependencies:2021.0.0")
    }
}

kotlin {
    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(Dependencies.Server.SPRING_WEBFLUX) {
        exclude("org.springframework.boot", "spring-boot-starter-json")
    }
    implementation(Dependencies.Server.SPRING_RSOCKET)
    implementation(Dependencies.Server.SPRING_SECURITY_RSOCKET)
    implementation(Dependencies.Server.SPRING_OAUTH_RESOURCE_SERVER)
    implementation(Dependencies.Server.SPRING_DATA_R2DBC)
    implementation(Dependencies.Server.SPRING_DATA_REDIS_REACTIVE)
    implementation(Dependencies.Server.SPRING_SECURITY)
    implementation(Dependencies.Server.SPRING_CLOUD_SLEUTH)
    implementation(Dependencies.Server.SPRING_CLOUD_GCP_STARTER)
    implementation(Dependencies.Server.KOTLIN)
    implementation(Dependencies.Server.KOTLIN_REFLECT)
    implementation(Dependencies.Server.KOTLIN_COROUTINES)
    implementation(Dependencies.Server.KOTLIN_COROUTINES_REACTOR)
    implementation(Dependencies.Server.KOTLIN_COROUTINES_JDK8)
    implementation(Dependencies.Server.SPRING_DATA_JDBC)
    implementation(Dependencies.Server.LIQUIBASE)
    implementation(Dependencies.Server.JWT)
    implementation(Dependencies.Server.KOTLIN_SERIALIZATION_JSON)
    implementation(Dependencies.Server.APACHE_TIKA)

    implementation(Dependencies.Server.REACTOR_KOTLIN)

    implementation(Dependencies.Server.PHONE_NUMBER)
    implementation(Dependencies.Server.FIREBASE_ADMIN)

    kapt(AnnotationProcessorsDependencies.Server.SPRING_CONFIGURATION_PROCESSOR)

    developmentOnly(DevelopmentDependencies.Server.SPRING_DEVTOOLS)

    runtimeOnly(RuntimeDependencies.Server.MYSQL)
    runtimeOnly(RuntimeDependencies.Server.R2DBC_MYSQL)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}