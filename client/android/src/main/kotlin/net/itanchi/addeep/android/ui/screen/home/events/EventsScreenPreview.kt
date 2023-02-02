package net.itanchi.addeep.android.ui.screen.home.events

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.itanchi.addeep.core.data.model.Event
import net.itanchi.addeep.core.data.model.MessageType
import net.itanchi.addeep.core.data.model.Status

@Preview
@Composable
fun EventListPreview() {
    EventList(
        events = listOf(
            Event(
                id = 1,
                name = "Event 1",
                description = "",
                imageUrl = "",
                applyOn = MessageType.Sticker,
                status = Status.Active,
            ),
            Event(
                id = 1,
                name = "Event 1",
                description = "",
                imageUrl = "",
                applyOn = MessageType.Sticker,
                status = Status.Active,
            ),
            Event(
                id = 1,
                name = "Event 1",
                description = "",
                imageUrl = "",
                applyOn = MessageType.Sticker,
                status = Status.Active,
            ),
        )
    ) {}
}

@Preview
@Composable
fun EventRowPreview() {
    EventRow(
        event = Event(
            id = 1,
            name = "Event 1",
            description = "",
            imageUrl = "",
            applyOn = MessageType.Sticker,
            status = Status.Active,
        )
    ) {}
}