package net.itanchi.addeep.android.ui.screen.home.friends.search

sealed class SearchFriendEvent {
    object Close : SearchFriendEvent()
    object OpenCountryList : SearchFriendEvent()
    data class SearchById(val addeepId: String) : SearchFriendEvent()
    data class SearchByPhone(val phone: String) : SearchFriendEvent()
    data class AddFriend(val friendId: Long) : SearchFriendEvent()
    object OpenAddeepId : SearchFriendEvent()
}