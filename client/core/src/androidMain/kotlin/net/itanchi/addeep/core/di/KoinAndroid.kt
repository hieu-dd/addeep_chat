package net.itanchi.addeep.core.di

import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.engine.okhttp.*
import kotlinx.coroutines.runBlocking
import net.itanchi.addeep.core.data.api.ApiConfig
import net.itanchi.addeep.core.data.repository.AuthRepo
import net.itanchi.addeep.core.db.Addeep
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformModule: Module = module {
    single {
        OkHttp.create {
            config {
                retryOnConnectionFailure(true)
            }
        }
    }
    single<SqlDriver> {
        AndroidSqliteDriver(Addeep.Schema, get(), "Addeep")
    }

    single(named("Coil")) {
        val authRepo = get<AuthRepo>()
        val apiConfig = get<ApiConfig>()
        Interceptor { chain ->
            val request = chain.request()
            chain.proceed(
                request.newBuilder()
                    .apply {
                        url(
                            if(request.url.host == "localhost") {
                                request.url.newBuilder()
                                    .scheme(if (apiConfig.secure) "https" else "http")
                                    .host(apiConfig.host)
                                    .port(apiConfig.port)
                                    .build()
                            } else {
                                request.url
                            }

                        )
                        runBlocking { authRepo.getAuthToken() }?.let {
                            header("Authorization", "Bearer $it")
                        }
                    }
                    .build()
            )
        }
    }
    single(named("Coil")) {
        OkHttpClient.Builder()
            .addInterceptor(get<Interceptor>(named("Coil")))
            .build()
    }
}