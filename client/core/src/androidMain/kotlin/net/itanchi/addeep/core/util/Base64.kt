package net.itanchi.addeep.core.util

import android.util.Base64

internal actual fun encodeBase64(string: String): String {
    return Base64.encodeToString(string.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
}