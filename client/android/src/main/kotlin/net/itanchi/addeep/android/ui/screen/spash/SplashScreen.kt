package net.itanchi.addeep.android.ui.screen.spash

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.itanchi.addeep.android.R
import net.itanchi.addeep.android.util.FlowAsState
import net.itanchi.addeep.android.util.ViewState
import org.koin.androidx.compose.getViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SplashScreen(
    viewModel: SplashViewModel = getViewModel(),
) {
    val splashViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.splashViewState,
        viewModel.splashViewState.value,
    )
    SplashScreenContent(
        viewModel::handleEvent,
        splashViewState,
    )
}

@Composable
fun SplashScreenContent(
    event: (SplashEvent) -> Unit,
    splashViewState: ViewState,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painterResource(R.drawable.placeholder),
                contentDescription = null,
                contentScale = ContentScale.Inside,
                modifier = Modifier.fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(horizontal = 36.dp, vertical = 48.dp)
            )

            Text(
                stringResource(R.string.auth_title),
                modifier = Modifier.padding(horizontal = 24.dp),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                stringResource(R.string.auth_description),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )

            Spacer(modifier = Modifier.weight(1f))

            if (splashViewState is ViewState.Success<*> && splashViewState.data == null) {
                Button(
                    onClick = { event(SplashEvent.Continue) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 64.dp)
                ) {
                    Text(text = stringResource(R.string.common_continue))
                }
            }
        }
    }
}