package net.itanchi.addeep.android.ui.screen.home.contacts

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.itanchi.addeep.core.data.model.User

@Preview
@Composable
fun ContactListPreview() {
    ContactList(
        contacts = listOf(
            User(id = 1, name = "User 1", phone = "", email = ""),
            User(id = 2, name = "User 2", phone = "", email = ""),
            User(id = 3, name = "User 3", phone = "", email = ""),
        ),{}
    ) {}
}

@Preview
@Composable
fun ContactRowPreview() {
    ContactRow(
        contact = User(id = 1, name = "User 1", phone = "", email = "")
    ) {}
}