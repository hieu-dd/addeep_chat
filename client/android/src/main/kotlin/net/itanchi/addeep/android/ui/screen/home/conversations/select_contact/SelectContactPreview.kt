package net.itanchi.addeep.android.ui.screen.home.conversations.select_contact

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.core.data.model.User

@Preview
@Composable
fun ContactListPreview() {
    SelectContactScreenContent(
        {}, ViewState.Success(
            listOf(
                User(id = 1, name = "User 1", phone = "", email = ""),
                User(id = 2, name = "User 2", phone = "", email = ""),
                User(id = 3, name = "User 3", phone = "", email = ""),
            )
        )
    )
}