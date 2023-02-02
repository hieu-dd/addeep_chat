package net.itanchi.addeep.server.config.rsocket

import io.rsocket.core.RSocketServer
import io.rsocket.core.Resume
import org.springframework.boot.rsocket.server.RSocketServerCustomizer
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("resumption")
@Component
class RSocketServerResumptionConfig : RSocketServerCustomizer {
    override fun customize(
        rSocketServer: RSocketServer
    ) {
        rSocketServer.resume(Resume())
    }
}