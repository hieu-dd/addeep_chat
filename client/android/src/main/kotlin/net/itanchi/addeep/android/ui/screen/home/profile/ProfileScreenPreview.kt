package net.itanchi.addeep.android.ui.screen.home.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.itanchi.addeep.core.data.model.User

@Preview
@Composable
fun ProfileInfoListPreview() {
    ProfileInfoList(
        User(
            id = 1,
            name = "User 1",
            displayName = "User 1",
            phone = "+84389104xxx",
            email = "",
            isMe = true,
        )
    )
}