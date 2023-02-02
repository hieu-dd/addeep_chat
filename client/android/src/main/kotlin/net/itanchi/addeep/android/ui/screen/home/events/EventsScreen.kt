package net.itanchi.addeep.android.ui.screen.home.events

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.itanchi.addeep.android.ui.screen.common.EmptyPage
import net.itanchi.addeep.android.ui.screen.common.ErrorPage
import net.itanchi.addeep.android.ui.screen.common.LoadingPage
import net.itanchi.addeep.android.util.FlowAsState
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.core.data.model.Event
import org.koin.androidx.compose.getViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun EventsScreen(
    viewModel: EventsViewModel = getViewModel(),
) {
    val eventsViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.eventsViewState,
        viewModel.eventsViewState.value,
    )
    EventsScreenContent(
        event = viewModel::handleEvent,
        eventsViewState = eventsViewState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreenContent(
    event: (EventsEvent) -> Unit,
    eventsViewState: ViewState,
) {
    Surface {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                SmallTopAppBar(title = {
                    Text(
                        "Events",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    )
                })
            },
        ) {
            when (eventsViewState) {
                is ViewState.Loading -> {
                    LoadingPage()
                }
                is ViewState.Error -> {
                    ErrorPage(
                        eventsViewState.cause?.message ?: "Unexpected Error"
                    )
                }
                is ViewState.Success<*> -> {
                    eventsViewState.data?.let {
                        if ((it as List<Event>).isNotEmpty()) {
                            EventList(it) { event -> event(EventsEvent.ViewEvent(event)) }
                        } else {
                            EmptyPage("No Events found")
                        }
                    } ?: EmptyPage("No Events found")
                }
                else -> {}
            }
        }
    }
}

@Composable
fun EventList(
    events: List<Event>,
    onItemClick: (Event) -> Unit,
) {
    LazyColumn {
        items(events) { event ->
            EventRow(event) { onItemClick(it) }
            Divider()
        }
    }
}

@Composable
fun EventRow(
    event: Event,
    onClick: (Event) -> Unit,
) {
    Row(
        Modifier
            .clickable { onClick(event) }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            Icons.Rounded.EmojiEvents,
            contentDescription = null,
            contentScale = ContentScale.Inside,
            modifier = Modifier.size(48.dp)
                .border(1.5.dp, MaterialTheme.colorScheme.tertiary, CircleShape)
                .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
        )

        Text(
            text = event.name,
            modifier = Modifier.padding(start = 16.dp).weight(1F),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}