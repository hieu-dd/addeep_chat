package net.itanchi.addeep.android.ui.screen.home.points

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import net.itanchi.addeep.android.ui.screen.common.EmptyPage
import net.itanchi.addeep.android.ui.screen.common.ErrorPage
import net.itanchi.addeep.android.ui.screen.common.LoadingPage
import net.itanchi.addeep.android.util.*
import net.itanchi.addeep.core.data.model.Point
import org.koin.androidx.compose.getViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun PointsHistoryScreen(
    viewModel: PointsHistoryViewModel = getViewModel(),
) {
    val pointsHistoryViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.pointsHistoryViewState,
        viewModel.pointsHistoryViewState.value,
    )
    PointsHistoryScreenContent(
        event = viewModel::handleEvent,
        pointsHistoryViewState = pointsHistoryViewState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsHistoryScreenContent(
    event: (PointsHistoryEvent) -> Unit,
    pointsHistoryViewState: ViewState,
) {
    Surface {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                SmallTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { event(PointsHistoryEvent.Close) }) {
                            Icon(Icons.Rounded.Close, contentDescription = null)
                        }
                    },
                    title = { Text("Points History") }
                )
            },
        ) {
            when (pointsHistoryViewState) {
                is ViewState.Loading -> {
                    LoadingPage()
                }
                is ViewState.Error -> {
                    ErrorPage(
                        pointsHistoryViewState.cause?.message ?: "Unexpected Error"
                    )
                }
                is ViewState.Success<*> -> {
                    pointsHistoryViewState.data?.let {
                        if ((it as List<Point>).isNotEmpty()) {
                            PointList(it) { event(PointsHistoryEvent.LoadMoreHistory) }
                        } else {
                            EmptyPage("No Points found")
                        }
                    } ?: EmptyPage("No Points found")
                }
                else -> {}
            }
        }
    }
}

@Composable
fun PointList(
    points: List<Point>,
    onLoadMoreHistory: () -> Unit,
) {
    val scrollState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
    ) {
        for (index in points.indices) {
            val prevPoint = points.getOrNull(index - 1)
            val nextPoint = points.getOrNull(index + 1)
            val point = points[index]

            if (prevPoint == null || !prevPoint.createdAt.isSameDate(point.createdAt)) {
                item {
                    DayHeader(point.createdAt.humanReadableDate())
                }
            }

            item {
                PointRow(point)
            }

            if (nextPoint != null && nextPoint.createdAt.isSameDate(point.createdAt)) {
                item {
                    Divider()
                }
            }
        }
    }

    InfiniteListHandler(scrollState, onLoadMore = onLoadMoreHistory)
}

@Composable
private fun DayHeader(dayString: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(
            text = dayString,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PointRow(
    point: Point,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = point.createdAt.humanReadableTime(),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = point.actionType.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Row {
                Text(
                    text = "Points",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f).alignByBaseline(),
                )
                Text(
                    text = if (point.point >= 0) "+${point.point}" else point.point.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.alignByBaseline()
                )
            }
        }
    }
}