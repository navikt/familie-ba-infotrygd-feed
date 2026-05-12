package no.nav.familie.ba.infotrygd.feed.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtAudienceValidator
import org.springframework.security.oauth2.jwt.JwtClaimValidator
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
    @param:Value("\${GYLDIG_SERVICE_BRUKER}") private val gyldigServiceBruker: String?,
) : JwtDecoder {
    private val delegate by lazy {
        require(!stsIssuer.isNullOrBlank()) { "STS_ISSUER må være satt" }
        require(!gyldigServiceBruker.isNullOrBlank()) { "GYLDIG_SERVICE_BRUKER må være satt" }

        val decoder = NimbusJwtDecoder.withIssuerLocation(stsIssuer).build()
        decoder.setJwtValidator(
            DelegatingOAuth2TokenValidator(
                JwtValidators.createDefaultWithIssuer(stsIssuer),
                JwtAudienceValidator(gyldigServiceBruker),
            ),
        )
        decoder
    }

    override fun decode(token: String?): Jwt? = delegate.decode(token)
}
