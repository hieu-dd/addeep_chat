package net.itanchi.addeep.server.repository.sticker

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface StickerRepo : CoroutineCrudRepository<StickerModel, Long> {
    suspend fun findByStickerPackAndImageFile(
        stickerPack: Long,
        imageFile: String
    ): StickerModel?
}