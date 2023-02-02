package net.itanchi.addeep.android.ui.screen.home.more

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import net.itanchi.addeep.android.R
import net.itanchi.addeep.android.util.FlowAsState
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.core.data.model.User
import org.koin.androidx.compose.getViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    viewModel: MoreViewModel = getViewModel(),
) {
    val moreViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.moreViewState,
        viewModel.moreViewState.value,
    )
    MoreScreenContent(
        viewModel::handleEvent,
        moreViewState,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MoreScreenContent(
    event: (MoreEvent) -> Unit,
    moreViewState: ViewState,
) {
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                SmallTopAppBar(
                    title = {
                        Text(
                            "More",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        )
                    },
                    actions = {
                        IconButton(onClick = { event(MoreEvent.NavigateToSettings) }) {
                            Icon(painterResource(R.drawable.ic_settings_line), contentDescription = null)
                        }
                    }
                )
            }
        ) {
            moreViewState.takeIf { it is ViewState.Success<*> && it.data != null }?.let {
                val user = (it as ViewState.Success<*>).data as User
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    Box(
                        modifier = Modifier.size(96.dp),
                        contentAlignment = Alignment.BottomEnd,
                    ) {
                        AsyncImage(
                            ImageRequest.Builder(context = context)
                                .data(user.getAvatarUrl())
                                .placeholder(R.drawable.placeholder_avatar)
                                .error(R.drawable.placeholder_avatar)
                                .fallback(R.drawable.placeholder_avatar)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(32.dp)),
                            contentScale = ContentScale.Crop,
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Current Points: ${user.pointInfo?.balance ?: 0}",
                        modifier = Modifier.padding(start = 16.dp),
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    MoreInfoRow(
                        icon = Icons.Rounded.Edit,
                        label = "Edit Account",
                        isOpenNewScreen = true,
                        onClick = { event(MoreEvent.NavigateToProfile) }
                    )

                    MoreInfoRow(
                        icon = Icons.Rounded.History,
                        label = "Point History",
                        isOpenNewScreen = true,
                        onClick = { event(MoreEvent.NavigateToPointsHistory) }
                    )

                    MoreInfoRow(
                        icon = Icons.Rounded.AccountBalanceWallet,
                        label = "Withdraw Point",
                        isOpenNewScreen = true,
                        onClick = { }
                    )
                }
            }
        }
    }

}

@Composable
fun MoreInfoRow(
    icon: ImageVector,
    label: String,
    isOpenNewScreen: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.padding(16.dp),
            tint = MaterialTheme.colorScheme.onSurface,
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(top = 16.dp),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                if (isOpenNewScreen) {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.padding(top = 16.dp, end = 16.dp),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Divider(modifier = Modifier.padding(top = 16.dp))
        }
    }
}
