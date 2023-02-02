package net.itanchi.addeep.server.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import net.itanchi.addeep.server.utils.converters.toDecodedToken
import org.springframework.stereotype.Component

@Component
class FirebaseAuthenticationService : AuthenticationServiceInterface {
    companion object {
        const val PHONE_NUMBER_CLAIMS_KEY = "phone_number"
    }

    override fun decodeToken(token: String): DecodedToken? {
        return try {
            FirebaseAuth.getInstance().verifyIdToken(token)?.toDecodedToken()
        } catch (exception: FirebaseAuthException) {
            exception.printStackTrace()
            null
        }
    }

    override fun getPhoneNumberClaimsKey(): String {
        return PHONE_NUMBER_CLAIMS_KEY
    }
}