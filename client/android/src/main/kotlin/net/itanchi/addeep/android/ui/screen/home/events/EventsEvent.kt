package net.itanchi.addeep.android.ui.screen.home.events

import net.itanchi.addeep.core.data.model.Event

sealed class EventsEvent {
    data class ViewEvent(val event: Event) : EventsEvent()
}