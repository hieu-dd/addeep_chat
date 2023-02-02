package net.itanchi.addeep.server.config.storage

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class StorageConfig {
    @Value("\${spring.cloud.gcp.credentials.encoded-key}")
    private val gcpEncodedKey: String = ""

    @Bean
    fun cloudStorage(
        cloudProject: String,
        credentials: ServiceAccountCredentials
    ): Storage {
        return StorageOptions.newBuilder()
            .setProjectId(cloudProject)
            .setCredentials(credentials)
            .build()
            .service
    }

    @Bean
    fun cloudProject(): String {
        return "addeep"
    }

    @Bean
    fun serviceAccounts(): ServiceAccountCredentials {
        return ServiceAccountCredentials.fromStream(Base64.getDecoder().decode(gcpEncodedKey).inputStream())
    }
}