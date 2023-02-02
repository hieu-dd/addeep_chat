package net.itanchi.addeep.core.data.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import net.itanchi.addeep.core.db.AuthQueries
import net.itanchi.addeep.core.util.transactionWithContext

internal class AuthRepo(
    private val tokenQueries: AuthQueries,
    private val backgroundDispatcher: CoroutineDispatcher,
) {
    suspend fun getAuthToken() = tokenQueries.find()
        .asFlow()
        .mapToOneOrNull(backgroundDispatcher)
        .firstOrNull()?.auth_token

    suspend fun saveAuthToken(
        authToken: String,
    ) = tokenQueries.transactionWithContext(backgroundDispatcher) {
        tokenQueries.upsertAuthToken(authToken, Clock.System.now().toEpochMilliseconds())
    }

    suspend fun getPushToken() = tokenQueries.find()
        .asFlow()
        .mapToOneOrNull(backgroundDispatcher)
        .firstOrNull()?.push_token

    suspend fun savePushToken(
        pushToken: String,
    ) = tokenQueries.transactionWithContext(backgroundDispatcher) {
        tokenQueries.upsertPushToken(pushToken, Clock.System.now().toEpochMilliseconds())
    }

    suspend fun delete() = tokenQueries.transactionWithContext(backgroundDispatcher) {
        tokenQueries.delete()
    }
}