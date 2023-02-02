package net.itanchi.addeep.server.config.service

import net.itanchi.addeep.server.config.YmlPropertySourceFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource(value = ["classpath:services.yml"], factory = YmlPropertySourceFactory::class)
class ServicesLoader

@ConfigurationProperties(prefix = "services")
@ConstructorBinding
data class Services(val properties: List<ServiceProperty>) {

    data class ServiceProperty(
        val id: ServiceID,
        val baseUrl: String,
        val timeoutMillis: Long = 3000L,
        val configs: Map<String, String> = mutableMapOf(),
    ) {
        enum class ServiceID {
            ADDEEP,
            GIPHY,
        }
    }
}