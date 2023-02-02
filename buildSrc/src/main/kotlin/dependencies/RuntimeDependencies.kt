package dependencies

/**
 * Configuration version of all runtime dependencies
 */
object RuntimeDependenciesVersions

/**
 * Project runtime dependencies, makes it easy to include external binaries or
 * other library modules to build.
 */
object RuntimeDependencies {
    object Server {
        const val R2DBC_MYSQL = "dev.miku:r2dbc-mysql"
        const val MYSQL = "mysql:mysql-connector-java"
    }
}