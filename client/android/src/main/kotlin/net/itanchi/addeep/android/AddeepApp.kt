package net.itanchi.addeep.android

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import co.touchlab.kermit.Logger
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.util.DebugLogger
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.data.ContactsLoader
import net.itanchi.addeep.android.data.MediaLoader
import net.itanchi.addeep.android.data.StickersLoader
import net.itanchi.addeep.android.ui.navigation.NavigationManager
import net.itanchi.addeep.android.ui.screen.MainViewModel
import net.itanchi.addeep.android.ui.screen.auth.email.EmailViewModel
import net.itanchi.addeep.android.ui.screen.auth.login.LoginViewModel
import net.itanchi.addeep.android.ui.screen.auth.login.countries.CountriesViewModel
import net.itanchi.addeep.android.ui.screen.auth.otp.OTPViewModel
import net.itanchi.addeep.android.ui.screen.auth.register.RegisterViewModel
import net.itanchi.addeep.android.ui.screen.home.chat.ChatViewModel
import net.itanchi.addeep.android.ui.screen.home.contacts.ContactsViewModel
import net.itanchi.addeep.android.ui.screen.home.conversations.ConversationsViewModel
import net.itanchi.addeep.android.ui.screen.home.conversations.select_contact.SelectContactViewModel
import net.itanchi.addeep.android.ui.screen.home.createId.AddeepIdViewModel
import net.itanchi.addeep.android.ui.screen.home.events.EventsViewModel
import net.itanchi.addeep.android.ui.screen.home.friends.invite.InviteFriendsViewModel
import net.itanchi.addeep.android.ui.screen.home.friends.invite.contacts.InviteViaContactsViewModel
import net.itanchi.addeep.android.ui.screen.home.friends.search.SearchFriendViewModel
import net.itanchi.addeep.android.ui.screen.home.more.MoreViewModel
import net.itanchi.addeep.android.ui.screen.home.points.PointsHistoryViewModel
import net.itanchi.addeep.android.ui.screen.home.profile.ProfileViewModel
import net.itanchi.addeep.android.ui.screen.home.settings.SettingsViewModel
import net.itanchi.addeep.android.ui.screen.home.webview.WebViewViewModel
import net.itanchi.addeep.android.ui.screen.permission.PermissionViewModel
import net.itanchi.addeep.android.ui.screen.spash.SplashViewModel
import net.itanchi.addeep.android.ui.screen.spash.tos.TermsOfServiceViewModel
import net.itanchi.addeep.android.util.AnimatedWebPDecoder
import net.itanchi.addeep.core.di.initKoin
import net.itanchi.addeep.core.util.AppInfo
import okhttp3.OkHttpClient
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

class AddeepApp : Application(), LifecycleObserver {
    private val scope = MainScope()

    private val stickersLoader: StickersLoader by inject()
    private val okHttpClient: OkHttpClient by inject(named("Coil"))

    companion object {
        var isAppInForeground: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()
        initKoin()
        initCoil()

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                isAppInForeground = true
                Logger.d("AddeepApp: foregrounded: ${ProcessLifecycleOwner.get().lifecycle.currentState.name}")
            }

            override fun onStop(owner: LifecycleOwner) {
                isAppInForeground = false
                Logger.d("AddeepApp: backgrounded: ${ProcessLifecycleOwner.get().lifecycle.currentState.name}")
            }
        })

        scope.launch {
            stickersLoader.initStickerPacks()
            stickersLoader.loadStickerPacks()
        }
    }

    private fun initKoin() {
        initKoin(
            module {
                single {
                    AppInfo(
                        appVersion = BuildConfig.VERSION_NAME,
                        type = "Mobile",
                        platformName = "Android",
                        platformVersion = Build.VERSION.RELEASE,
                        deviceName = "${Build.MANUFACTURER} ${Build.MODEL}",
                    )
                }
                single<Context> { this@AddeepApp }
                single { StickersLoader(get(), get()) }
                single { MediaLoader(get(), get()) }
                single { ContactsLoader(get()) }
                single { NavigationManager() }
                viewModel { MainViewModel() }
                viewModel { SplashViewModel() }
                viewModel { TermsOfServiceViewModel() }
                viewModel { LoginViewModel() }
                viewModel { CountriesViewModel() }
                viewModel { OTPViewModel() }
                viewModel { RegisterViewModel() }
                viewModel { EmailViewModel() }
                viewModel { PermissionViewModel() }
                viewModel { ContactsViewModel() }
                viewModel { ConversationsViewModel() }
                viewModel { EventsViewModel() }
                viewModel { MoreViewModel() }
                viewModel { PointsHistoryViewModel() }
                viewModel { ProfileViewModel() }
                viewModel { SettingsViewModel() }
                viewModel { WebViewViewModel() }
                viewModel { SelectContactViewModel() }
                viewModel {
                    ChatViewModel(
                        conversationId = get(),
                        userId = get(),
                        userName = get()
                    )
                }
                viewModel { SearchFriendViewModel() }
                viewModel { AddeepIdViewModel() }
                viewModel { InviteFriendsViewModel() }
                viewModel { InviteViaContactsViewModel() }
            }
        )
    }

    private fun initCoil() {
        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .okHttpClient(okHttpClient)
                .components {
                    add(VideoFrameDecoder.Factory())
                    if (SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                        add(AnimatedWebPDecoder.Factory())
                    }
                }
                .logger(DebugLogger())
                .respectCacheHeaders(false)
                .build()
        )
    }
}