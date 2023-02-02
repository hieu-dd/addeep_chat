package net.itanchi.addeep.android.ui.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import net.itanchi.addeep.android.BuildConfig
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.ui.navigation.NavigationManager
import net.itanchi.addeep.android.ui.screen.auth.email.EmailScreen
import net.itanchi.addeep.android.ui.screen.auth.login.LoginScreen
import net.itanchi.addeep.android.ui.screen.auth.login.countries.CountriesScreen
import net.itanchi.addeep.android.ui.screen.auth.otp.OTPScreen
import net.itanchi.addeep.android.ui.screen.auth.register.RegisterScreen
import net.itanchi.addeep.android.ui.screen.home.HomeScreen
import net.itanchi.addeep.android.ui.screen.home.chat.ChatScreen
import net.itanchi.addeep.android.ui.screen.home.chat.toConversationId
import net.itanchi.addeep.android.ui.screen.home.chat.toUserId
import net.itanchi.addeep.android.ui.screen.home.chat.toUserName
import net.itanchi.addeep.android.ui.screen.home.conversations.select_contact.SelectContactScreen
import net.itanchi.addeep.android.ui.screen.home.createId.AddeepIdScreen
import net.itanchi.addeep.android.ui.screen.home.friends.invite.InviteFriendScreen
import net.itanchi.addeep.android.ui.screen.home.friends.invite.InviteType
import net.itanchi.addeep.android.ui.screen.home.friends.invite.contacts.InviteViaContactsScreen
import net.itanchi.addeep.android.ui.screen.home.friends.search.SearchFriendScreen
import net.itanchi.addeep.android.ui.screen.home.points.PointsHistoryScreen
import net.itanchi.addeep.android.ui.screen.home.profile.ProfileScreen
import net.itanchi.addeep.android.ui.screen.home.settings.SettingsScreen
import net.itanchi.addeep.android.ui.screen.home.webview.WebViewScreen
import net.itanchi.addeep.android.ui.screen.permission.PermissionScreen
import net.itanchi.addeep.android.ui.screen.spash.SplashScreen
import net.itanchi.addeep.android.ui.screen.spash.tos.TermsOfServiceScreen
import net.itanchi.addeep.android.util.getType
import org.koin.androidx.compose.inject
import java.io.File

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navigationManager by inject<NavigationManager>()
    val context = LocalContext.current
    LaunchedEffect(navigationManager.commands) {
        navigationManager.commands.collect { commands ->
            val destination = commands.createDestination()
            if (destination.isNotEmpty()) {
                when (commands) {
                    is NavigationDirections.PhoneSettings -> {
                        context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        })
                    }
                    is NavigationDirections.FileViewer -> {
                        try {
                            val file = File(commands.path)
                            val uri =
                                FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
                            val mime = context.getType(uri)
                            val intent = Intent().apply {
                                action = Intent.ACTION_VIEW
                                setDataAndType(uri, mime)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(intent)
                        } catch (error: Throwable) {
                            // ignore
                        }
                    }
                    is NavigationDirections.Back -> {
                        navController.popBackStack()
                    }
                    is NavigationDirections.FinishWithResults -> {
                        navController.previousBackStackEntry?.savedStateHandle?.apply {
                            commands.data.forEach { (key, value) ->
                                this.set(key, value)
                            }
                        }
                        navController.popBackStack()
                    }
                    else -> navController.navigate(destination, commands.options)
                }
            }
        }
    }

    NavHost(
        navController,
        startDestination = NavigationDirections.Splash.destination,
        route = NavigationDirections.Root.destination,
    ) {
        addSplashGraph()
        addAuthGraph()
        addPermissionGraph()
        addHomeGraph()
        addSettingsGraph()
        addProfileGraph()
        addPointsHistoryGraph()
        addChatGraph()
        addWebViewGraph()
        addSelectContactGraph()
        addSearchFriendGraph()
        addAddeepIdGraph()
        addInviteFriendsGraph()
    }
}

private fun NavGraphBuilder.addSplashGraph() {
    composable(NavigationDirections.Splash.destination) {
        SplashScreen()
    }
    composable(NavigationDirections.TermsOfService.destination) {
        TermsOfServiceScreen()
    }
}

private fun NavGraphBuilder.addAuthGraph() {
    navigation(
        startDestination = NavigationDirections.AuthLogin.destination,
        route = NavigationDirections.Auth.destination,
    ) {
        composable(NavigationDirections.AuthLogin.destination) { backStack ->
            LoginScreen(backStack.savedStateHandle)
        }
        composable(NavigationDirections.CountryPicker.destination) {
            CountriesScreen()
        }
        with(NavigationDirections.AuthOTP()) {
            composable(
                destination,
                arguments,
            ) { backStack ->
                with(backStack.arguments) {
                    val phone = this?.getString("phone") ?: ""
                    OTPScreen(phone = phone)
                }
            }
        }
        composable(NavigationDirections.AuthRegister.destination) {
            RegisterScreen()
        }
        composable(NavigationDirections.AuthEmail.destination) {
            EmailScreen()
        }
    }
}

private fun NavGraphBuilder.addPermissionGraph() {
    composable(NavigationDirections.Permission.destination) {
        PermissionScreen()
    }
}

private fun NavGraphBuilder.addHomeGraph() {
    composable(NavigationDirections.Home.destination) { backStack ->
        HomeScreen(backStack.savedStateHandle)
    }
}

private fun NavGraphBuilder.addSettingsGraph() {
    composable(NavigationDirections.Settings.destination) {
        SettingsScreen()
    }
}

private fun NavGraphBuilder.addSelectContactGraph() {
    composable(NavigationDirections.SelectContact.destination) {
        SelectContactScreen()
    }
}

private fun NavGraphBuilder.addProfileGraph() {
    composable(NavigationDirections.Profile.destination) {
        ProfileScreen()
    }
}

private fun NavGraphBuilder.addPointsHistoryGraph() {
    composable(NavigationDirections.PointsHistory.destination) {
        PointsHistoryScreen()
    }
}

private fun NavGraphBuilder.addChatGraph() {
    val chatDirection = NavigationDirections.Chat()
    composable(
        chatDirection.destination,
        chatDirection.arguments,
    ) { backStack ->
        with(backStack.arguments) {
            val conversationId = (this?.getLong("conversationId") ?: 0).toConversationId()
            val userId = (this?.getLong("userId") ?: 0).toUserId()
            val userName = (this?.getString("userName").orEmpty()).toUserName()
            ChatScreen(conversationId = conversationId, userId = userId, userName = userName)
        }
    }
}

private fun NavGraphBuilder.addWebViewGraph() {
    val webViewDirection = NavigationDirections.WebView("")
    composable(
        webViewDirection.destination,
        webViewDirection.arguments,
    ) { backStack ->
        with(backStack.arguments) {
            val url = this?.getString("url") ?: ""
            WebViewScreen(url)
        }
    }
}

private fun NavGraphBuilder.addSearchFriendGraph() {
    composable(NavigationDirections.SearchFriend.destination) { backStack ->
        SearchFriendScreen(backStack.savedStateHandle)
    }
}

private fun NavGraphBuilder.addAddeepIdGraph() {
    composable(NavigationDirections.AddeepId.destination) {
        AddeepIdScreen()
    }
}

private fun NavGraphBuilder.addInviteFriendsGraph() {
    navigation(
        startDestination = NavigationDirections.InviteFriend.destination,
        route = NavigationDirections.Invite.destination
    ){
        composable(NavigationDirections.InviteFriend.destination) {
            InviteFriendScreen()
        }
        with(NavigationDirections.InviteViaContacts()) {
            composable(
                destination,
                arguments,
            ) { backStack ->
                with(backStack.arguments) {
                    val inviteType = this?.getString("inviteType")?.let { InviteType.valueOf(it) } ?: InviteType.SMS
                    InviteViaContactsScreen(inviteType = inviteType)
                }
            }
        }
    }
}