package net.itanchi.addeep.android.ui.screen.home.friends.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.itanchi.addeep.core.data.model.User

@Preview
@Composable
fun SearchIdContentPreview() {
    SearchIdContent(null, {}, {})
}

@Preview
@Composable
fun SearchPhonePreview() {
    SearchPhoneNumberContent(countryCode = "kr", onSearchPhoneNumber = {}, onOpenCountryPicker = {})
}

@Preview
@Composable
fun SearchedFriendPreview() {
    SearchedFriendContent({}, User())
}