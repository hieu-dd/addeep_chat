package net.itanchi.addeep.android.ui.screen.auth.email

import net.itanchi.addeep.core.data.model.Preferences


sealed class EmailEvent {
    data class AddEmail(
        val email: String,
        val preferences: Preferences,
    ) : EmailEvent()

    object Skip : EmailEvent()

    object DismissError: EmailEvent()
}