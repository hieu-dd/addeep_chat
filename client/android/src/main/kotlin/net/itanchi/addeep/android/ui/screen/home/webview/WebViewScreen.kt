package net.itanchi.addeep.android.ui.screen.home.webview

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.koin.androidx.compose.getViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    url: String,
    viewModel: WebViewViewModel = getViewModel(),
) {
    WebViewScreenContent(
        event = viewModel::handleEvent,
        url = url,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreenContent(
    event: (WebViewEvent) -> Unit,
    url: String,
) {
    val screenTitle = remember { mutableStateOf("Loading...") }
    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                SmallTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { event(WebViewEvent.GoBack) }) {
                            Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                        }
                    },
                    title = { Text(text = screenTitle.value) }
                )
            }
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        webViewClient = WebViewClient()
                        webChromeClient = object : WebChromeClient() {
                            override fun onReceivedTitle(view: WebView, title: String) {
                                screenTitle.value = title
                            }
                        }
                        loadUrl(url)
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { it.loadUrl(url) }
            )
        }
    }

}