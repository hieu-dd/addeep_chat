package extensions

import org.gradle.api.artifacts.dsl.RepositoryHandler

/**
 * Adds all default repositories used to access to the different declared dependencies
 */
fun RepositoryHandler.applyDefault() {
    mavenLocal()
    google()
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
}