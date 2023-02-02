package net.itanchi.addeep.core.util

actual fun String.unicode() = with(this) {
    val bytes = split('-').map { it.toInt(16) }.toIntArray()
    String(bytes, offset = 0, length = count { it == '-' } + 1)
}