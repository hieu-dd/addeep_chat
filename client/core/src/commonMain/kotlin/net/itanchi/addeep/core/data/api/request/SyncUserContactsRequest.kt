package net.itanchi.addeep.core.data.api.request

import kotlinx.serialization.Serializable
import net.itanchi.addeep.core.data.model.Contact

@Serializable
internal data class SyncUserContactsRequest(
    val contacts: List<Contact>
)