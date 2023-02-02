package net.itanchi.addeep.core.util

import java.util.*

internal actual fun randomUUID(): String {
    return UUID.randomUUID().toString()
}