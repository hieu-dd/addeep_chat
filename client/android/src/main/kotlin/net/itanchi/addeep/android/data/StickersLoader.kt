package net.itanchi.addeep.android.data

import android.content.Context
import android.content.res.AssetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.itanchi.addeep.core.data.model.StickerPack
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


/**
 * Load all stickers inside app data
 *
 * The data structure:
 * /
 *  stickers
 *          /sticker_id
 *                     /sticker_id.png <-- tray image
 *                     /xxx.webp <-- stickers
 *                     /contents.json <-- sticker pack info
 */
class StickersLoader(
    private val context: Context,
    private val json: Json,
) {
    private val assets: AssetManager = context.assets
    private val stickersPath = "stickers"

    private val _stickerPacks: MutableList<StickerPack> = mutableListOf()

    val stickerPacks: List<StickerPack> = _stickerPacks

    suspend fun initStickerPacks() = coroutineScope {
        withContext(Dispatchers.IO) {
            try {
                assets.list(stickersPath)?.forEach {
                    assets.open("$stickersPath${File.separator}$it").use { stickerPack ->
                        unzip(stickerPack, File(context.filesDir, stickersPath))
                    }
                }
            } catch (error: Throwable) {
                error.printStackTrace()
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun loadStickerPacks(force: Boolean = false) = coroutineScope {
        withContext(Dispatchers.IO) {
            if (force || _stickerPacks.isEmpty()) {
                try {
                    val stickersDir = File(context.filesDir, stickersPath)
                    stickersDir.list()?.forEach {
                        val stickerDir = File(stickersDir, it)
                        File(stickerDir, "contents.json").inputStream().use { inputStream ->
                            val stickerPack = json.decodeFromStream<StickerPack>(inputStream)
                            _stickerPacks.add(
                                stickerPack.copy(
                                    trayImageFile = File(stickerDir, stickerPack.trayImageFile).path,
                                    stickers = stickerPack.stickers.map { sticker ->
                                        sticker.copy(imageFile = File(stickerDir, sticker.imageFile).path).apply {
                                            message = "${stickerPack.id}/${sticker.imageFile}"
                                        }
                                    }
                                )
                            )
                        }
                    }
                } catch (error: Throwable) {
                    error.printStackTrace()
                }
            }
        }
    }

    private fun unzip(zipFile: InputStream, outputDir: File) {
        outputDir.takeIf { !it.exists() }?.mkdir()

        var entry: ZipEntry?
        var readLen: Int
        val readBuffer = ByteArray(4096)

        ZipInputStream(zipFile).use { zipInputStream ->
            while (zipInputStream.nextEntry.also { entry = it } != null) {
                val file = File(outputDir, entry!!.name)
                if (entry!!.isDirectory) {
                    file.takeIf { !it.exists() }?.mkdir()
                } else {
                    file.takeIf { !it.exists() }?.createNewFile()
                    FileOutputStream(file).use { outputStream ->
                        while (zipInputStream.read(readBuffer).also { readLen = it } != -1) {
                            outputStream.write(readBuffer, 0, readLen)
                        }
                    }
                }
            }
        }
    }

}