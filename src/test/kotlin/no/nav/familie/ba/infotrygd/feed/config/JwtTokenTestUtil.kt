package no.nav.familie.ba.infotrygd.feed.config

import no.nav.security.mock.oauth2.MockOAuth2Server

object JwtTokenTestUtil {
    fun lagStsToken(
        mockOAuth2Server: MockOAuth2Server,
        subject: String = "srvfamilie-ba-infotrygd-feed",
        audience: String? = null,
        tilleggsClaims: Map<String, Any> =
            mapOf(
                "aud" to
                    listOf(
                        "aud1",
                        "aud-localhost",
                    ),
            ),
    ): String =
        mockOAuth2Server
            .issueToken(
                issuerId = "sts",
                subject = subject,
                audience = audience,
                claims = tilleggsClaims,
            ).serialize()

    fun lagAzureAdToken(
        mockOAuth2Server: MockOAuth2Server,
        subject: String = "test-user@nav.no",
        audience: String = "aud-localhost",
        groups: List<String> = emptyList(),
        tilleggsClaims: Map<String, Any> = emptyMap(),
    ): String {
        val claims =
            mapOf(
                "groups" to groups,
                "preferred_username" to subject,
            ) + tilleggsClaims

        return mockOAuth2Server
            .issueToken(
                issuerId = "azuread",
                subject = subject,
                audience = audience,
                claims = claims,
            ).serialize()
    }
}
