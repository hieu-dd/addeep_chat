package net.itanchi.addeep.android.ui.screen.home.points

sealed class PointsHistoryEvent {
    object Close : PointsHistoryEvent()
    object LoadMoreHistory : PointsHistoryEvent()
}