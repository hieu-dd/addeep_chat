package net.itanchi.addeep.server.repository.contact

import com.google.i18n.phonenumbers.NumberParseException
import net.itanchi.addeep.server.service.contact.Contact
import net.itanchi.addeep.server.service.user.User
import net.itanchi.addeep.server.utils.PhoneNumberUtils
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("contacts")
data class ContactModel(
    @Id
    @Column("id")
    var id: Long = 0,

    @Column("name")
    var name: String,

    @Column("phone")
    var phone: String?,

    @Column("email")
    var email: String?,

    @Column("created_at")
    @CreatedDate
    var createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    var updatedAt: Instant = Instant.now(),
) : Persistable<Long> {

    @Transient
    var isNewRecord = true

    override fun getId() = id

    override fun isNew() = isNewRecord

    fun update(newContactInfo: Contact) = apply {
        name = newContactInfo.name
        phone = newContactInfo.phone
        email = newContactInfo.email
        isNewRecord = false
        updatedAt = Instant.now()
    }

    companion object {
        fun fromContact(contact: Contact) = with(contact) {
            ContactModel(
                id = id,
                name = name,
                phone = phone,
                email = email,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        }
    }

    fun isSameContactWith(contact: Contact) = (!phone.isNullOrEmpty() && phone == contact.phone)
            || (!email.isNullOrEmpty() && email == contact.email)
}

fun List<ContactModel>.extractPhoneNumbers(): List<String> {
    return mapNotNull { contact ->
        contact.phone?.let {
            try {
                val phoneNumber = PhoneNumberUtils.parse(it)
                phoneNumber.nationalNumber.toString()
            } catch (exception: NumberParseException) {
                it.removePrefix("0")
            } catch (exception: Throwable) {
                null
            }
        }
    }
}

fun List<ContactModel>.findDisplayNameByUser(user: User) =
    find { it.email == user.email || it.phone == user.phone }?.name.orEmpty()