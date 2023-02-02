import io.ktor.utils.io.core.*
import io.rsocket.kotlin.ExperimentalMetadataApi
import io.rsocket.kotlin.metadata.CompositeMetadata
import io.rsocket.kotlin.metadata.RoutingMetadata
import io.rsocket.kotlin.metadata.metadata
import io.rsocket.kotlin.metadata.security.BearerAuthMetadata
import io.rsocket.kotlin.payload.Payload
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.payload.data
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMetadataApi::class)
@ExperimentalSerializationApi
internal inline fun <reified T> Json.encodeToPayload(
    route: String,
    token: String,
    value: T?,
): Payload = buildPayload {
    value?.let { data(encodeToString(it)) } ?: data(ByteReadPacket.Empty)
    metadata(CompositeMetadata(RoutingMetadata(route), BearerAuthMetadata(token)))
}

@ExperimentalSerializationApi
internal inline fun <reified T> Json.decodeFromPayload(
    payload: Payload,
): T = decodeFromString(payload.data.readText())