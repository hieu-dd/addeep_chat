package net.itanchi.addeep.core.data.api

data class ApiConfig internal constructor(
    val host: String,
    val port: Int,
    val secure: Boolean,
)

class ApiConfigBuilder internal constructor() {
    var host: String? = null
    var port: Int? = null
    var secure: Boolean = false

    fun build(): ApiConfig {
        return when {
            host == null -> throw Throwable("host must be set")
            port == null -> throw Throwable("port must be set")
            else -> ApiConfig(host!!, port!!, secure)
        }
    }
}

fun apiConfig(
    block: ApiConfigBuilder.() -> Unit
): ApiConfig = ApiConfigBuilder().apply(block).build()
