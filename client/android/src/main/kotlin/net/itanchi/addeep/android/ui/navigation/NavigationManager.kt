package net.itanchi.addeep.android.ui.navigation

import kotlinx.coroutines.flow.MutableSharedFlow

class NavigationManager {
    val commands = MutableSharedFlow<NavigationCommand>()

    suspend fun navigate(
        directions: NavigationCommand
    ) {
        commands.emit(directions)
    }
}