package net.itanchi.addeep.server.utils

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber

object PhoneNumberUtils {
    private val phoneNumberUtil = PhoneNumberUtil.getInstance()

    @JvmStatic
    fun parse(phone: String): Phonenumber.PhoneNumber = phoneNumberUtil.parse(phone, null)
}