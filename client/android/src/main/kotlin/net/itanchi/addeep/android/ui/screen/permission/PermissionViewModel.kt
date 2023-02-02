package net.itanchi.addeep.android.ui.screen.permission

import kotlinx.coroutines.launch
import net.itanchi.addeep.android.ui.base.BaseViewModel
import net.itanchi.addeep.android.ui.navigation.NavigationDirections

class PermissionViewModel : BaseViewModel() {

    fun handleEvent(event: PermissionEvent) {
        scope.launch {
            when (event) {
                is PermissionEvent.NavigateToPhoneSettings -> navigationManager.navigate(NavigationDirections.PhoneSettings)
                is PermissionEvent.NavigateToHome -> navigationManager.navigate(NavigationDirections.Home)
            }
        }
    }
}
