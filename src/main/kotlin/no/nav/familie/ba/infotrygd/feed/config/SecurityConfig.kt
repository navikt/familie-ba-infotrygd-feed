package no.nav.familie.ba.infotrygd.feed.config

import no.nav.familie.sikkerhet.context.FamilieFellesSpringSecurityKonfigurasjon
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Import(FamilieFellesSpringSecurityKonfigurasjon::class)
class SecurityConfig(
    private val azureDecoder: AzureDecoder,
    private val stsDecoder: StsDecoder,
) {
    /**
     * Filter chain for Azure AD endpoints - krever Azure AD token.
     * Høyere prioritet (@Order(1)) slik at denne matcher før default.
     */
    @Bean
    @Order(1)
    fun azureSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            securityMatcher(
                "/api/barnetrygd/v1/feedazure",
                "/api/barnetrygd/v1/feedazure",
                "/api/barnetrygd/v1/feed/*/opprettet",
            )

            authorizeHttpRequests {
                authorize(anyRequest, authenticated)
            }

            oauth2ResourceServer {
                jwt { jwtDecoder = azureDecoder }
            }
            csrf { disable() }
        }

        return http.build()
    }

    /**
     * Default filter chain - krever STS token (legacy).
     * Laveste prioritet (@Order(LOWEST_PRECEDENCE)) slik at den matcher alt annet.
     */
    @Bean
    @Order(LOWEST_PRECEDENCE)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize("/internal/**", permitAll)
                authorize("/swagger-ui/**", permitAll)
                authorize("/v3/api-docs/**", permitAll)

                authorize(anyRequest, authenticated)
            }
            oauth2ResourceServer {
                jwt { jwtDecoder = stsDecoder }
            }
            csrf { disable() }
        }

        return http.build()
    }
}
