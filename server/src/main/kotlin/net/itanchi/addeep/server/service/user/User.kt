package net.itanchi.addeep.server.service.user

import net.itanchi.addeep.server.repository.user.Gender
import java.time.Instant
import java.time.LocalDate

data class User(
    val id: Long = 0,
    val addeepId: String? = null,
    val phone: String,
    val email: String? = null,
    val avatar: String? = null,
    val gender: Gender? = null,
    val dob: LocalDate? = null,
    val name: String,
    val allowToSearchByAddeepId: Boolean = false,
    val isActive: Boolean = false,
    val isReported: Boolean = false,
    val isBlocked: Boolean = false,
    val preferences: String = "",
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
) {
    var displayName: String = ""
}