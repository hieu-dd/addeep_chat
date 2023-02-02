package net.itanchi.addeep.server.config.client

data class UserAgent(
    val appVersion: String,
    val type: String,
    val platformName: String,
    val platformVersion: String,
    val deviceName: String,
) {
    companion object {
        fun parseString(userAgent: String): UserAgent? {
            return Regex("^([a-zA-Z]+/[0-9].[0-9].[0-9]) \\(([a-zA-Z]+); ([a-zA-Z]+) ([0-9]+); (.+)\\)\$")
                .find(userAgent)
                ?.let {
                    val (appVersion, type, platformName, platformVersion, deviceName) = it.destructured
                    UserAgent(
                        appVersion = appVersion,
                        type = type,
                        platformName = platformName,
                        platformVersion = platformVersion,
                        deviceName = deviceName,
                    )
                }
        }
    }
}