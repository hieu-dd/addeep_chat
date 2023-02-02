package net.itanchi.addeep.server.service.contact

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import net.itanchi.addeep.server.exception.Error
import net.itanchi.addeep.server.repository.contact.*
import net.itanchi.addeep.server.repository.user.UserModel
import net.itanchi.addeep.server.repository.user.UserRepo
import net.itanchi.addeep.server.repository.user.toUser
import net.itanchi.addeep.server.service.user.User
import org.springframework.stereotype.Component

@Component
class ContactService(
    private val contactRepo: ContactRepo,
    private val userContactRepo: UserContactRepo,
    private val userRepo: UserRepo
) {
    suspend fun saveUserContacts(
        userId: Long,
        contacts: List<Contact>
    ): List<User> = coroutineScope {
        val existedContacts = findUserContacts(userId)
        val updatedContacts = mutableListOf<ContactModel>()
        val newContacts = mutableListOf<ContactModel>()
        contacts.forEach { contact ->
            existedContacts.find { it.isSameContactWith(contact) }?.let {
                updatedContacts.add(it.update(contact))
            } ?: let {
                newContacts.add(ContactModel.fromContact(contact))
            }
        }
        var savedContacts = listOf<ContactModel>()
        awaitAll(
            async {
                updateContacts(updatedContacts)
            },
            async {
                savedContacts = saveNewContacts(
                    contacts = newContacts,
                    userId = userId,
                )
            }
        )
        val finalContacts = existedContacts + savedContacts
        findUsersByContacts(finalContacts)
            .distinctBy { it.id }
            .mapNotNull {
                it.takeIf { it.id != userId }
                    ?.toUser()
                    ?.apply { displayName = finalContacts.findDisplayNameByUser(this) }
            }
    }

    private suspend fun updateContacts(contacts: List<ContactModel>) {
        contacts.takeIf { it.isNotEmpty() }?.let {
            contactRepo.saveAll(it).toList()
        }
    }

    private suspend fun saveNewContacts(
        contacts: List<ContactModel>,
        userId: Long,
    ): List<ContactModel> {
        val savedContacts = contacts.takeIf { it.isNotEmpty() }?.let {
            contactRepo.saveAll(it).toList()
        }.orEmpty()
        savedContacts.takeIf { it.isNotEmpty() }?.let {
            it.map { savedContact ->
                UserContactModel(
                    userId = userId,
                    contactId = savedContact.id
                )
            }.also {
                userContactRepo.saveAll(it).toList()
            }
        }
        return savedContacts
    }

    private suspend fun findUserContacts(
        userId: Long,
    ): List<ContactModel> {
        val userContacts = userContactRepo.findAllByUserId(userId)
        val contactIds = userContacts.map { it.contactId }
        return contactRepo.findAllById(contactIds).toList()
    }

    private suspend fun findUsersByContacts(contacts: List<ContactModel>): List<UserModel> {
        return userRepo.findAllByPhoneNumberInOrEmailIn(
            emails = contacts.mapNotNull { it.email },
            phones = contacts.extractPhoneNumbers(),
        )
    }

    suspend fun verifyFriend(
        userId: Long,
        friendUser: User
    ): Boolean {
        return userId == friendUser.id || findUserContacts(userId).any { it.phone == friendUser.phone || it.email == friendUser.email }
    }

    suspend fun addContact(
        userId: Long,
        contactUserId: Long
    ): User? {
        if (userId == contactUserId) throw Error.InvalidUser
        val users = userRepo.findAllById(ids = setOf(userId, contactUserId)).toList()
        val user = users.first()
        val contactUser = users.getOrNull(1) ?: throw Error.UserNotFound
        if (verifyFriend(userId = userId, friendUser = contactUser.toUser())) throw Error.AddContactError.AlreadyInContacts
        val contacts = contactRepo.saveAll(
            listOf(
                ContactModel(
                    name = user.name.orEmpty(),
                    phone = user.getPhone(),
                    email = user.email
                ),
                ContactModel(
                    name = contactUser.name.orEmpty(),
                    phone = contactUser.getPhone(),
                    email = contactUser.email
                )
            )
        ).toList()
        userContactRepo.saveAll(
            listOf(
                UserContactModel(
                    userId = userId,
                    contactId = contacts[1].id
                ),
                UserContactModel(
                    userId = contactUserId,
                    contactId = contacts[0].id
                )
            )
        ).toList()
        return contactUser.toUser()
    }
}