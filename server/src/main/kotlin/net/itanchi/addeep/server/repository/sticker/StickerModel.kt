package net.itanchi.addeep.server.repository.sticker

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("stickers")
data class StickerModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("sticker_pack")
    val stickerPack: Long = 0,

    @Column("image_file")
    val imageFile: String = "",

    @Column("emoji")
    val emoji: List<String> = listOf(),

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)
