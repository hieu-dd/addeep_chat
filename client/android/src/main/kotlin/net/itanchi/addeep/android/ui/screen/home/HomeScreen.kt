package net.itanchi.addeep.android.ui.screen.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import net.itanchi.addeep.android.ui.navigation.NavigationManager
import net.itanchi.addeep.android.ui.screen.home.contacts.ContactsScreen
import net.itanchi.addeep.android.ui.screen.home.conversations.ConversationsScreen
import net.itanchi.addeep.android.ui.screen.home.events.EventsScreen
import net.itanchi.addeep.android.ui.screen.home.more.MoreScreen
import org.koin.androidx.compose.inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(homeSavedStateHandle: SavedStateHandle) {
    val navController = rememberNavController()
    val navigationManager by inject<NavigationManager>()

    LaunchedEffect(navigationManager.commands) {
        navigationManager.commands.collect { commands ->
            val destination = commands.createDestination()
            if (destination.startsWith("home_")) {
                navController.navigate(destination, commands.options)
            }
        }
    }

    Scaffold(
        bottomBar = {
            HomeNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeNavigationItem.Conversations.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(HomeNavigationItem.Contacts.route) {
                ContactsScreen()
            }
            composable(HomeNavigationItem.Conversations.route) {
                ConversationsScreen(homeSavedStateHandle)
            }
            composable(HomeNavigationItem.Events.route) {
                EventsScreen()
            }
            composable(HomeNavigationItem.More.route) {
                MoreScreen()
            }
        }
    }
}

@Composable
fun HomeNavigationBar(
    navController: NavHostController
) {
    val items = listOf(
        HomeNavigationItem.Contacts,
        HomeNavigationItem.Conversations,
        HomeNavigationItem.Events,
        HomeNavigationItem.More,
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            val label = stringResource(item.label)
            var icon = painterResource(item.icon)
            var iconColor = MaterialTheme.colorScheme.onSurfaceVariant
            if (isSelected) {
                icon = painterResource(item.selectedIcon)
                iconColor = MaterialTheme.colorScheme.primary
            }

            NavigationBarItem(
                icon = { Icon(icon, label, tint = iconColor) },
                label = { Text(label, maxLines = 1) },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}
