package net.itanchi.addeep.server.rest.user.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.itanchi.addeep.server.repository.user.Gender
import net.itanchi.addeep.server.rest.user.request.Preferences
import net.itanchi.addeep.server.service.point.PointInfo
import net.itanchi.addeep.server.service.user.User
import net.itanchi.addeep.server.utils.serializers.LocalDateSerializer
import java.time.LocalDate

@Serializable
data class UserDTO(
    val id: Long,
    val addeepId: String? = null,
    val email: String?,
    val name: String,
    val phone: String,
    val gender: Gender? = null,
    @Serializable(with = LocalDateSerializer::class)
    val dob: LocalDate? = null,
    val avatar: String? = null,
    val allowToSearchByAddeepId: Boolean = false,
    val isFriend: Boolean? = null,
    val pointInfo: PointInfoDTO,
    val preferences: Preferences? = null
) {
    var displayName: String = ""

    companion object {
        fun fromUser(
            user: User,
            pointInfo: PointInfo = PointInfo(),
            includePreferences: Boolean = false,
            isFriend: Boolean? = null
        ) = with(user) {
            UserDTO(
                id = id,
                addeepId = addeepId,
                email = email,
                name = name,
                gender = gender,
                dob = dob,
                phone = phone,
                avatar = avatar,
                isFriend = isFriend,
                allowToSearchByAddeepId = allowToSearchByAddeepId,
                pointInfo = PointInfoDTO.fromPointInfo(pointInfo),
                preferences = preferences.takeIf { includePreferences && preferences.isNotBlank() }
                    ?.let { Json.decodeFromString(it) }
            ).apply {
                displayName = user.displayName
            }
        }
    }
}