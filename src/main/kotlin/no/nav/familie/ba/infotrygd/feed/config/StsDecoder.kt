package no.nav.familie.ba.infotrygd.feed.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.stereotype.Component

/**
 * JwtDecoder for legacy STS tokens.
 * Brukes for eldre systemintegrasjoner som fortsatt bruker STS.
 */
@Component
class StsDecoder(
    @param:Value("\${STS_ISSUER}") private val stsIssuer: String?,
    @param:Value("\${GYLDIG_SERVICE_BRUKER}") private val gyldigServiceBrukere: String?,
) : JwtDecoder {
    private val delegate by lazy {
        require(!stsIssuer.isNullOrBlank()) { "STS_ISSUER må være satt" }
        require(!gyldigServiceBrukere.isNullOrBlank()) { "GYLDIG_SERVICE_BRUKER må være satt" }

        val decoder = NimbusJwtDecoder.withIssuerLocation(stsIssuer).build()
        decoder.setJwtValidator(
            DelegatingOAuth2TokenValidator(
                JwtValidators.createDefaultWithIssuer(stsIssuer),
                audienceAnyValidator(gyldigServiceBrukere),
            ),
        )
        decoder
    }

    /**
     * GYLDIG_SERVICE_BRUKER er ofte en liste, for å støtte at audiencene i tokenet matcher minst én av service-brukerne i
     * lista må vi lage en custom validator
     */
    private fun audienceAnyValidator(gyldigeServiceBrukere: String): OAuth2TokenValidator<Jwt> {
        val allowedAudiences =
            gyldigeServiceBrukere
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toSet()

        return OAuth2TokenValidator { jwt ->
            val tokenAudiences = jwt.audience.toSet()
            val hasMatch = tokenAudiences.any { it in allowedAudiences }

            if (hasMatch) {
                OAuth2TokenValidatorResult.success()
            } else {
                OAuth2TokenValidatorResult.failure(
                    OAuth2Error(
                        OAuth2ErrorCodes.INVALID_TOKEN,
                        "Token audience matcher ingen gyldige servicebrukere",
                        null,
                    ),
                )
            }
        }
    }

    override fun decode(token: String?): Jwt? = delegate.decode(token)
}
