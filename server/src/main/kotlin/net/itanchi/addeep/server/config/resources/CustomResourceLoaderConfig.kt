package net.itanchi.addeep.server.config.resources

import org.springframework.cloud.gcp.storage.GoogleStorageProtocolResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.ResourceLoader

@Configuration
class CustomResourceLoaderConfig {
    @Bean
    fun customResourceLoader(
        googleCloudProtocolResolver: GoogleStorageProtocolResolver,
    ): ResourceLoader {
        return DefaultResourceLoader().apply {
            addProtocolResolver(googleCloudProtocolResolver)
        }
    }
}