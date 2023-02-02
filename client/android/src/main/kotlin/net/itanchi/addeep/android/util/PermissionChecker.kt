package net.itanchi.addeep.android.util

import android.Manifest.permission.*
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionChecker(
    private val context: Context,
) {
    fun canReadContacts(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun canReadExternalStorage(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}