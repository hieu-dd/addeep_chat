package net.itanchi.addeep.server.auth

data class UserRecord(
    val uid: String,
    val displayName: String,
    val email: String?,
    val phoneNumber: String,
    val photoUrl: String,
)
