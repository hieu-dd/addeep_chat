package net.itanchi.addeep.core.di

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.auth.*
import io.ktor.client.features.auth.providers.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.rsocket.kotlin.core.RSocketConnector
import io.rsocket.kotlin.core.WellKnownMimeType
import io.rsocket.kotlin.keepalive.KeepAlive
import io.rsocket.kotlin.payload.PayloadMimeType
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.payload.data
import io.rsocket.kotlin.transport.ktor.client.RSocketSupport
import kotlinx.coroutines.Dispatchers
import net.itanchi.addeep.core.data.AppDataManager
import net.itanchi.addeep.core.data.AppMediaManager
import net.itanchi.addeep.core.data.api.*
import net.itanchi.addeep.core.data.repository.AuthRepo
import net.itanchi.addeep.core.data.repository.ConversationRepo
import net.itanchi.addeep.core.data.repository.UserRepo
import net.itanchi.addeep.core.db.Addeep
import net.itanchi.addeep.core.util.AppInfo
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import co.touchlab.kermit.Logger as KLogger
import kotlinx.serialization.json.Json as KJson

fun initKoin(appModule: Module): KoinApplication {
    val koinApplication = startKoin {
        modules(
            appModule,
            platformModule,
            coreModule,
        )
    }

    return koinApplication
}

expect val platformModule: Module

private val coreModule = module {
    // DB
    single { Addeep(get()) }
    // Token Repo
    single { AuthRepo(get<Addeep>().authQueries, Dispatchers.Default) }
    // User Repo
    single { UserRepo(get<Addeep>().userQueries, get<Addeep>().participantQueries, Dispatchers.Default) }
    // Conversation Repo
    single {
        ConversationRepo(
            get<Addeep>().conversationQueries,
            get<Addeep>().messageQueries,
            Dispatchers.Default,
            get(),
        )
    }
    // Json
    single {
        KJson {
            isLenient = false
            ignoreUnknownKeys = true
            encodeDefaults = true
            coerceInputValues = true
        }
    }
    single {
        ApiConfig(
            // Local Device
//            host = "192.168.1.9",
//            port = 8080,
            // Local Emulator
//            host = "10.0.2.2",
//            port = 8080,
            // Dev
            host = "34.124.160.55",
            port = 80,
            secure = false
        )
    }
    // API client
    single<Logger> {
        object : Logger {
            override fun log(message: String) {
                KLogger.v(message)
            }
        }
    }
    single(named("auth")) {
        HttpClient(get()) {
            defaultApiConfig(this@single)
        }
    }
    single(named("default")) {
        HttpClient(get()) {
            defaultApiConfig(this@single)
            defaultApiAuth(this@single)
        }
    }

    single(named("media")) {
        HttpClient(get()) {
            defaultApiConfig(this@single, false)
            defaultApiAuth(this@single)
        }
    }

    single(named("rSocket")) {
        HttpClient(get()) {
            WebSockets {}
            install(RSocketSupport) {
                connector = RSocketConnector {
                    connectionConfig {
                        keepAlive = KeepAlive(30 * 1000, 120 * 1000)
                        payloadMimeType = PayloadMimeType(
                            data = WellKnownMimeType.ApplicationJson,
                            metadata = WellKnownMimeType.MessageRSocketCompositeMetadata
                        )
                        setupPayload {
                            buildPayload {
                                data("hello")
                            }
                        }
                    }
                    reconnectable { _, attempt -> attempt <= 3 }
                }
            }
        }
    }
    // User API
    single<UsersApi> {
        UsersApiImpl(
            get(named("default")),
            get(named("media")),
        )
    }
    // Conversation API
    single<ConversationApi> {
        ConversationApiImpl(
            get(named("default")),
            get(named("media")),
            get(named("rSocket")),
            get(),
            get(),
        )
    }
    // Media API
    single<MediaApi> {
        MediaApiImpl(
            get(named("media")),
        )
    }
    // Point API
    single<PointApi> {
        PointApiImpl(
            get(named("default")),
        )
    }
    // Event API
    single<EventApi> {
        EventApiImpl(
            get(named("default")),
        )
    }
    // Data manager
    single { AppDataManager() }
    single { AppMediaManager() }
}

private fun HttpClientConfig<*>.defaultApiConfig(
    scope: Scope,
    predefinedContentType: Boolean = true
) {
    expectSuccess = false

    defaultRequest {
        val appInfo = scope.get<AppInfo>()
        header(HttpHeaders.UserAgent, appInfo.formattedInfo())
        if (predefinedContentType) header(HttpHeaders.ContentType, ContentType.Application.Json)
        url {
            val apiConfig = scope.get<ApiConfig>()
            host = apiConfig.host
            port = apiConfig.port
            protocol = if (apiConfig.secure) URLProtocol.HTTPS else URLProtocol.HTTP
        }
    }
    Json {
        serializer = KotlinxSerializer(scope.get())
    }
    Logging {
        logger = scope.get()
        level = LogLevel.ALL
    }
}

private fun HttpClientConfig<*>.defaultApiAuth(
    scope: Scope,
) {
    Auth {
        val authRepo = scope.get<AuthRepo>()
        bearer {
            loadTokens {
                authRepo.getAuthToken().takeIf { !it.isNullOrBlank() }?.let {
                    BearerTokens(accessToken = it, refreshToken = "")
                }
            }
            refreshTokens {
                if (it.status == HttpStatusCode.Unauthorized) {
                    authRepo.getAuthToken().takeIf { !it.isNullOrBlank() }?.let {
                        BearerTokens(accessToken = it, refreshToken = "")
                    }
                } else {
                    null
                }
            }
        }
    }
}