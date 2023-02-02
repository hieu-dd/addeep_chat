package net.itanchi.addeep.server.auth

data class DecodedToken(
    val uid: String,
    val name: String,
    val picture: String,
    val email: String?,
    val claims: Map<String, Any>
)
