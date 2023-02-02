package net.itanchi.addeep.server.repository.user

import net.itanchi.addeep.server.service.user.User
import net.itanchi.addeep.server.utils.PhoneNumberUtils
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.time.LocalDate

@Table("users")
data class UserModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("addeep_Id")
    var addeepId: String? = null,

    @Column("allow_to_search_by_addeep_id")
    var allowToSearchByAddeepId: Boolean = false,

    @Column("firebase_uid")
    val firebaseUid: String = "",

    @Column("phone_number")
    val phoneNumber: String,

    @Column("country_code")
    val countryCode: String,

    @Column("email")
    var email: String?,

    @Column("name")
    var name: String?,

    @Column("gender")
    var gender: Gender? = null,

    @Column("avatar")
    var avatar: String? = null,

    @Column("dob")
    var dob: LocalDate? = null,

    @Column("is_active")
    val isActive: Boolean = false,

    @Column("is_reported")
    val isReported: Boolean = false,

    @Column("is_blocked")
    val isBlocked: Boolean = false,

    @Column("preferences")
    var preferences: String = "",

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    var updatedAt: Instant = Instant.now(),
) {
    companion object {
        fun fromUser(user: User) = with(user) {
            val phoneNumber = PhoneNumberUtils.parse(phone)
            UserModel(
                id = id,
                phoneNumber = phoneNumber.nationalNumber.toString(),
                countryCode = phoneNumber.countryCode.toString(),
                email = email,
                name = name,
                isActive = isActive,
                isReported = isReported,
                isBlocked = isBlocked,
                preferences = preferences,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        }
    }

    fun getPhone() = if (countryCode.isNotEmpty() && phoneNumber.isNotEmpty()) "+$countryCode$phoneNumber" else ""
}

fun UserModel.toUser() = User(
    id = id,
    addeepId = addeepId,
    phone = getPhone(),
    email = email,
    name = name.orEmpty(),
    avatar = avatar,
    gender = gender,
    dob = dob,
    allowToSearchByAddeepId = allowToSearchByAddeepId,
    isActive = isActive,
    isReported = isReported,
    isBlocked = isBlocked,
    preferences = preferences,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

enum class Gender {
    Male,
    Female,
    Other
}