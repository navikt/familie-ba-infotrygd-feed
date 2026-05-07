package no.nav.familie.ba.infotrygd.feed.config

import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration

@Configuration
@ContextConfiguration(initializers = [MockOAuth2ServerInitializer::class])
class SecurityTestConfig
// TODO se om denne er nødvendig å ha?
