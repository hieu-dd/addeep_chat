package net.itanchi.addeep.server.utils.converters

import net.itanchi.addeep.server.repository.event.Status
import net.itanchi.addeep.server.repository.message.MessageType
import net.itanchi.addeep.server.repository.user.DeviceType
import java.time.Instant
import java.time.format.DateTimeParseException

fun String.toInstant(): Instant? = try {
    Instant.parse(this)
} catch (exception: DateTimeParseException) {
    null
}

internal fun String.toStatus(): Status? = when (lowercase()) {
    Status.Active.name.lowercase() -> Status.Active
    Status.InActive.name.lowercase() -> Status.InActive
    else -> null
}

internal fun String.toDeviceType(): DeviceType? = when (lowercase()) {
    DeviceType.Mobile.name.lowercase() -> DeviceType.Mobile
    DeviceType.Desktop.name.lowercase() -> DeviceType.Desktop
    else -> null
}

internal fun String.toMessageType(): MessageType? = when (lowercase()) {
    MessageType.PlainText.name.lowercase() -> MessageType.PlainText
    MessageType.Sticker.name.lowercase() -> MessageType.Sticker
    MessageType.Photo.name.lowercase() -> MessageType.Photo
    MessageType.Document.name.lowercase() -> MessageType.Document
    MessageType.Gif.name.lowercase() -> MessageType.Gif
    else -> null
}