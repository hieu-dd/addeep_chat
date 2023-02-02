package net.itanchi.addeep.android.ui.navigation

import androidx.navigation.*
import net.itanchi.addeep.android.ui.screen.home.friends.invite.InviteType
import java.net.URLEncoder

sealed class NavigationDirections {

    object Default : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = ""
    }

    object Root : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "root"
    }

    object Splash : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "splash"
    }

    object TermsOfService : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            popUpTo(Root.destination) {
                inclusive = true
            }
            launchSingleTop = true
        }
        override val destination: String = "terms_of_service"
    }

    object Auth : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            popUpTo(Root.destination) {
                inclusive = true
            }
            launchSingleTop = true
        }
        override val destination: String = "auth"
    }

    object AuthLogin : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            popUpTo(Root.destination) {
                inclusive = true
            }
            launchSingleTop = true
        }
        override val destination: String = "auth_login"
    }

    data class AuthOTP(
        val phone: String = "",
    ) : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf(
            navArgument("phone") {
                defaultValue = ""
                type = NavType.StringType
            },
        )
        override val options: NavOptions = navOptions {
            launchSingleTop = true
        }
        override val destination: String = "auth_otp?phone={phone}"
        override fun createDestination(): String {
            return "auth_otp?phone=${URLEncoder.encode(phone, Charsets.UTF_8.name())}"
        }
    }

    object AuthRegister : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            popUpTo(Root.destination) {
                inclusive = true
            }
            launchSingleTop = true
        }
        override val destination: String = "auth_register"
    }

    object AuthEmail : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            popUpTo(Root.destination) {
                inclusive = true
            }
            launchSingleTop = true
        }
        override val destination: String = "auth_email"
    }

    object Permission : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            popUpTo(Root.destination) {
                inclusive = true
            }
            launchSingleTop = true
        }
        override val destination: String = "permission"
    }


    object CountryPicker : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            launchSingleTop = true
        }
        override val destination: String = "country_picker"
    }

    object Home : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            popUpTo(Root.destination) {
                inclusive = true
            }
            launchSingleTop = true
        }
        override val destination: String = "home"
    }

    object HomeContacts : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "home_contacts"
    }

    object HomeConversations : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "home_conversations"
    }

    object HomeEvents : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "home_events"
    }

    object HomeMore : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "home_more"
    }

    object Profile : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "profile"
    }

    object PointsHistory : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "points_history"
    }

    object Settings : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            launchSingleTop = true
        }
        override val destination: String = "settings"
    }

    object SelectContact : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            launchSingleTop = true
        }
        override val destination: String = "select_contact"
    }

    data class Chat(
        val conversationId: Long = 0,
        val userId: Long = 0,
        val userName: String = ""
    ) : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf(
            navArgument("conversationId") {
                defaultValue = 0
                type = NavType.LongType
            },
            navArgument("userId") {
                defaultValue = 0
                type = NavType.LongType
            },
            navArgument("userName") {
                defaultValue = ""
                type = NavType.StringType
            },
        )
        override val options: NavOptions = navOptions {
            launchSingleTop = true
        }
        override val destination: String =
            "chat?conversationId={conversationId}&userId={userId}&userName={userName}"

        override fun createDestination(): String {
            return "chat?conversationId=$conversationId&userId=$userId&userName=$userName"
        }
    }

    data class WebView(
        val url: String,
    ) : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf(
            navArgument("url") {
                defaultValue = ""
                type = NavType.StringType
            },
        )
        override val options: NavOptions? = null
        override val destination: String = "webview?url={url}"
        override fun createDestination(): String {
            return "webview?url=$url"
        }
    }

    object SearchFriend : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "search_friend"
    }

    object AddeepId : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "addeep_id"
    }

    object Invite : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions = navOptions {
            popUpTo(Root.destination) {
                inclusive = true
            }
            launchSingleTop = true
        }
        override val destination: String = "invite"
    }

    object InviteFriend : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "invite_friend"
    }

    data class InviteViaContacts(
        val inviteType: InviteType = InviteType.SMS,
    ) : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf(
            navArgument("inviteType") {
                defaultValue = InviteType.SMS.name
                type = NavType.StringType
            },
        )
        override val options: NavOptions? = null
        override val destination: String = "invite_via_contacts?inviteType={inviteType}"
        override fun createDestination(): String {
            return "invite_via_contacts?inviteType=${inviteType}"
        }
    }

    // Back
    object Back : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "back"
    }

    // Back
    data class FinishWithResults(
        val data: Map<String, Any>,
    ) : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "back"
    }

    // External

    object PhoneSettings : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "phone_settings"
    }

    data class FileViewer(
        val path: String,
    ) : NavigationDirections(), NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "file_viewer"
    }
}
