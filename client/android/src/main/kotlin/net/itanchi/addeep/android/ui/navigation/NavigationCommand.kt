package net.itanchi.addeep.android.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavOptions

interface NavigationCommand {
    val arguments: List<NamedNavArgument>
    val options: NavOptions?
    val destination: String

    fun createDestination(): String = destination
}