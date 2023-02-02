package net.itanchi.addeep.android.ui.screen.home.webview

import kotlinx.coroutines.launch
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections

class WebViewViewModel : BaseViewModel() {

    fun handleEvent(event: WebViewEvent) {
        scope.launch {
            when (event) {
                is WebViewEvent.GoBack -> navigationManager.navigate(NavigationDirections.Back)
                else -> {}
            }
        }
    }
}
