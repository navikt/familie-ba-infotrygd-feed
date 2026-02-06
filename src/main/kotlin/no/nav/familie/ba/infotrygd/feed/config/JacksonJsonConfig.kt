package no.nav.familie.ba.infotrygd.feed.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper

@Configuration
class JacksonJsonConfig(
    private val jsonMapper: JsonMapper,
) {
    @Bean
    fun objectMapper(): ObjectMapper = jsonMapper
}
