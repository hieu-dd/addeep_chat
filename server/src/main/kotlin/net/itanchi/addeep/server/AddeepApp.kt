package net.itanchi.addeep.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class AddeepApp

fun main(args: Array<String>) {
    runApplication<AddeepApp>(*args)
}
