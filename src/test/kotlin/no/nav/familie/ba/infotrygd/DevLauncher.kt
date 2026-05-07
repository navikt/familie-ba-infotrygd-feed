package no.nav.familie.ba.infotrygd

import no.nav.familie.ba.infotrygd.feed.config.MockOAuth2ServerInitializer
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackages = ["no.nav.familie.ba.infotrygd.feed"])
class DevLauncher

fun main(args: Array<String>) {
    val springApp = SpringApplication(DevLauncher::class.java)
    springApp.setAdditionalProfiles("dev")
    springApp.addInitializers(MockOAuth2ServerInitializer())
    springApp.run(*args)
}
