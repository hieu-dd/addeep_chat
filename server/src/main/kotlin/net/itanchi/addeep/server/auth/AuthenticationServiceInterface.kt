package net.itanchi.addeep.server.auth

interface AuthenticationServiceInterface {
    fun decodeToken(token: String): DecodedToken?
    fun getPhoneNumberClaimsKey(): String
}