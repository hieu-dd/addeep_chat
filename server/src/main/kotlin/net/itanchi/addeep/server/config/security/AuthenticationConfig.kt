package net.itanchi.addeep.server.config.security

import net.itanchi.addeep.server.config.YmlPropertySourceFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource(value = ["classpath:authentication.yml"], factory = YmlPropertySourceFactory::class)
class SignatureLoader

@ConfigurationProperties(prefix = "authentication")
@ConstructorBinding
data class AuthenticationConfig(
    val secretKey: String,
)