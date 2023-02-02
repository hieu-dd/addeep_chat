package net.itanchi.addeep.server.config.database

import net.itanchi.addeep.server.config.database.converter.ByteArrayToUUIDConverter
import net.itanchi.addeep.server.config.database.converter.UUIDToByteArrayConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.MySqlDialect

@Configuration
class DatabaseConfig {
    @Bean
    fun customConversions(): R2dbcCustomConversions {
        return R2dbcCustomConversions.of(
            MySqlDialect.INSTANCE,
            mutableListOf(
                ByteArrayToUUIDConverter,
                UUIDToByteArrayConverter,
            )
        )
    }
}