package net.itanchi.addeep.server.service.contact

import java.time.Instant

data class Contact(
    val id: Long = 0,
    val name: String,
    val phone: String?,
    val email: String?,
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
) {
    val isNew: Boolean = true
}