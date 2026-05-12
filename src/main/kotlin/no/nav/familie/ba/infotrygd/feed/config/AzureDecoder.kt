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
 * JwtDecoder for Azure AD tokens.
 * Brukes for service-to-service og ansatt-autentisering.
 */
@Component
class AzureDecoder(
    @param:Value("\${AZURE_OPENID_CONFIG_ISSUER}") private val azureIssuer: String,
    @param:Value("\${AZURE_APP_CLIENT_ID}") private val azureClientId: String,
) : JwtDecoder {
    private val delegate by lazy {
        val decoder = NimbusJwtDecoder.withIssuerLocation(azureIssuer).build()
        decoder.setJwtValidator(
            DelegatingOAuth2TokenValidator(
                JwtValidators.createDefaultWithIssuer(azureIssuer),
                JwtAudienceValidator(azureClientId),
            )
        )
        decoder
    }


    override fun decode(token: String?): Jwt? = delegate.decode(token)
}
