package net.itanchi.addeep.server.utils.converters

import com.google.firebase.auth.FirebaseToken
import net.itanchi.addeep.server.auth.DecodedToken
import net.itanchi.addeep.server.auth.UserRecord
import com.google.firebase.auth.UserRecord as FirebaseUserRecord

fun FirebaseToken.toDecodedToken(): DecodedToken = DecodedToken(
    uid = uid,
    name = name.orEmpty(),
    picture = picture.orEmpty(),
    email = email,
    claims = claims
)

fun FirebaseUserRecord.toAppUserRecord(): UserRecord = UserRecord(
    uid = uid,
    displayName = displayName.orEmpty(),
    email = email,
    phoneNumber = phoneNumber,
    photoUrl = photoUrl.orEmpty(),
)