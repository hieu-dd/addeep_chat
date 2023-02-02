package net.itanchi.addeep.android.ui.screen.home

import net.itanchi.addeep.android.R
import net.itanchi.addeep.android.ui.navigation.NavigationDirections

sealed class HomeNavigationItem(
    val route: String,
    val icon: Int,
    val selectedIcon: Int,
    val label: Int,
) {
    object Contacts : HomeNavigationItem(
        NavigationDirections.HomeContacts.destination,
        R.drawable.ic_home_line,
        R.drawable.ic_home_fill,
        R.string.home_home,
    )

    object Conversations : HomeNavigationItem(
        NavigationDirections.HomeConversations.destination,
        R.drawable.ic_chat_line,
        R.drawable.ic_chat_fill,
        R.string.home_chats,
    )

    object Events : HomeNavigationItem(
        NavigationDirections.HomeEvents.destination,
        R.drawable.ic_celebration_line,
        R.drawable.ic_celebration_fill,
        R.string.home_events,
    )

    object More : HomeNavigationItem(
        NavigationDirections.HomeMore.destination,
        R.drawable.ic_more_horiz,
        R.drawable.ic_more_horiz,
        R.string.home_more,
    )
}