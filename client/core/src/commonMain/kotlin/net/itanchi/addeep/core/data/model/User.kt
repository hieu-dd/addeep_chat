package net.itanchi.addeep.core.data.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long = 0,
    val addeepId: String? = null,
    val name: String = "",
    val displayName: String = "",
    val phone: String = "",
    val email: String? = null,
    val gender: Gender? = null,
    val dob: LocalDate? = null,
    val pointInfo: PointInfo? = null,
    val preferences: Preferences? = null,
    var isMe: Boolean = false,
    val allowToSearchByAddeepId: Boolean = false,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
) {
    fun getAvatarUrl() = "http://localhost:8080/api/v1/users/avatar?userId=$id"
}

@Serializable
data class PointInfo(
    val receivedPoint: Long,
    val balance: Long,
)

@Serializable
data class Preferences(
    var collectAndUsePersonalInfo: Boolean? = null
)

@Serializable
enum class Gender {
    Male, Female, Other
}
