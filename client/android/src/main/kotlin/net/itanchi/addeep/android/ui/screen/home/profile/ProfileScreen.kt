package net.itanchi.addeep.android.ui.screen.home.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import net.itanchi.addeep.android.util.FlowAsState
import net.itanchi.addeep.android.util.ViewState
import net.itanchi.addeep.core.data.model.User
import org.koin.androidx.compose.getViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = getViewModel(),
) {
    val profileViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.profileViewState,
        viewModel.profileViewState.value,
    )
    ProfileScreenContent(
        viewModel::handleEvent,
        profileViewState,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ProfileScreenContent(
    event: (ProfileEvent) -> Unit,
    profileViewState: ViewState,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                SmallTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { event(ProfileEvent.GoBack) }) {
                            Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                        }
                    },
                    title = { Text("Profile") },
                )
            }
        ) {
            when (profileViewState) {
                is ViewState.Success<*> -> {
                    profileViewState.data?.let {
                        val user = it as User
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top,
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Spacer(modifier = Modifier.height(32.dp))

                            Box(
                                modifier = Modifier.wrapContentSize(),
                                contentAlignment = Alignment.BottomEnd,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .clip(RoundedCornerShape(48.dp))
                                        .background(MaterialTheme.colorScheme.onSurface, RoundedCornerShape(36.dp)),
                                    contentAlignment = Alignment.BottomEnd,
                                ) {
                                    Image(
                                        Icons.Rounded.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(144.dp),
                                        contentScale = ContentScale.Fit,
                                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface),
                                    )
                                }
                                Image(
                                    Icons.Rounded.PhotoCamera,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                                    contentScale = ContentScale.Inside,
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            ProfileInfoList(user)
                        }
                    }
                }
                else -> {}
            }
        }
    }

}

@Composable
fun ProfileInfoList(
    user: User,
) {
    Column {
        ProfileInfoRow(
            icon = Icons.Rounded.Person,
            label = "Name",
            content = user.name,
            isEditable = true,
            onClick = {},
        )

        ProfileInfoRow(
            icon = Icons.Rounded.Phone,
            label = "Phone",
            content = user.phone,
            isEditable = false,
            onClick = {},
        )

        ProfileInfoRow(
            icon = Icons.Rounded.Email,
            label = "Email",
            content = user.email.orEmpty(),
            isEditable = true,
            onClick = {},
        )
    }
}

@Composable
fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    content: String,
    isEditable: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onClick)
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

            Row {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(top = 16.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Text(
                        text = content,
                        modifier = Modifier.padding(top = 4.dp),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                if (isEditable) {
                    Icon(
                        Icons.Rounded.Edit,
                        contentDescription = null,
                        modifier = Modifier.padding(top = 16.dp, end = 16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Divider(modifier = Modifier.padding(top = 16.dp))
        }
    }
}
