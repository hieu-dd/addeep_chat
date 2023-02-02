package net.itanchi.addeep.android.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import net.itanchi.addeep.android.BuildConfig
import net.itanchi.addeep.core.data.model.FileInfo
import okio.Path.Companion.toPath
import java.io.File


fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun Context.getTmpFileUri(name: String, ext: String): Uri {
    val tmpFile = File.createTempFile(name, ext, cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }

    return FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
}

fun Context.getType(uri: Uri): String {
    return contentResolver.getType(uri).orEmpty()
}

fun Context.getName(uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor.use {
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result!!.lastIndexOf('/')
        if (cut != -1) {
            result = result!!.substring(cut + 1)
        }
    }
    return result!!
}

fun Context.loadFileInfo(uri: Uri): FileInfo {
    val contents = contentResolver.openInputStream(uri)!!.readBytes()
    val type = getType(uri)
    val name = getName(uri)
    return FileInfo(name, uri.toString().toPath(false), type, contents)
}

fun Context.sendSMS(phoneList: List<String>, message: String) {
    val sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse("smsto:${phoneList.joinToString(",")}"))
    sendIntent.putExtra("sms_body", message)
    startActivity(sendIntent)
}

fun Context.sendEmail(emailList: List<String>, message: String) {
    val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
    emailIntent.type = "message/rfc822"
    emailIntent.putExtra(Intent.EXTRA_EMAIL, emailList.toTypedArray())
    emailIntent.putExtra(Intent.EXTRA_TEXT, message)
    startActivity(emailIntent)
}

fun Context.shareAnotherApp(message: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, message)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)
}