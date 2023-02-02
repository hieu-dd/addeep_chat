package net.itanchi.addeep.server.rest.user.request

import kotlinx.serialization.Serializable
import net.itanchi.addeep.server.service.contact.Contact
import net.itanchi.addeep.server.utils.validators.EmailValidator
import net.itanchi.addeep.server.utils.validators.PhoneNumberValidator
import net.itanchi.addeep.server.utils.validators.Validator
import net.itanchi.addeep.server.utils.validators.validate

@Serializable
data class SyncContactsRequest(
    val contacts: List<LocalContact>
) {
    init {
        mutableListOf<Validator>().apply {
            contacts.forEach {
                it.emails = it.emails.mapNotNull { email -> email.takeIf { EmailValidator(email).validate() == null } }
                it.phones = it.phones.mapNotNull { phone -> phone.takeIf { PhoneNumberValidator(phone).validate() == null } }
            }
        }
    }

    @Serializable
    data class LocalContact(
        val name: String,
        var emails: List<String>,
        var phones: List<String>
    ) {
        fun toContacts(): List<Contact> {
            return mutableListOf<Contact>().apply {
                addAll(emails.distinct().map { email ->
                    Contact(
                        name = name,
                        phone = null,
                        email = email
                    )
                })
                addAll(phones.distinct().map { phone ->
                    Contact(
                        name = name,
                        phone = phone,
                        email = null
                    )
                })
            }

        }
    }
}