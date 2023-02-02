package net.itanchi.addeep.android.ui.screen

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.lifecycleScope
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.BuildConfig
import net.itanchi.addeep.android.ui.navigation.NavigationDirections
import net.itanchi.addeep.android.ui.navigation.NavigationManager
import net.itanchi.addeep.android.ui.theme.AppTheme
import net.itanchi.addeep.android.util.LocalBackPressedDispatcher
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModel()
    private val navigationManager: NavigationManager by inject()

    companion object {
        const val EXTRA_CONVERSATION_ID = "conversationId"

        fun getIntent(
            context: Context,
            conversationId: String,
        ): Intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_CONVERSATION_ID, conversationId)
        }

        fun getPendingIntent(
            context: Context,
            conversationId: String,
        ): PendingIntent = with(getIntent(context, conversationId)) {
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            PendingIntent.getActivity(context, 0, this, flags)
        }

        fun Intent.getConversationId(): Long {
            return extras?.get(EXTRA_CONVERSATION_ID)?.toString()?.toLong() ?: 0
        }

        fun Intent.removeConversationId() {
            removeExtra(EXTRA_CONVERSATION_ID)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            AppCenter.start(
                application,
                "5c13bae0-be3a-4dc8-a010-3f5ca2662c36",
                Analytics::class.java,
                Crashes::class.java
            )
        }
        setContent {
            CompositionLocalProvider(LocalBackPressedDispatcher provides onBackPressedDispatcher) {
                AppTheme {
                    MainScreen()
                }
            }
        }
        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        lifecycleScope.launch {
            navigationManager.navigate(
                NavigationDirections.Chat(
                    conversationId = intent.getConversationId()
                )
            )
        }
    }
}
