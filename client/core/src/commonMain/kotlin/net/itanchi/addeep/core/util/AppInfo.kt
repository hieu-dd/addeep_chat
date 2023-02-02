package net.itanchi.addeep.core.util

data class AppInfo(
    val appVersion: String,
    val type: String,
    val platformName: String,
    val platformVersion: String,
    val deviceName: String,
) {
    fun formattedInfo(): String = "addeep/$appVersion ($type; $platformName $platformVersion; $deviceName)"
}
