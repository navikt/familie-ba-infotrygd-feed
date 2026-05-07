package no.nav.familie.ba.infotrygd.feed.config

import no.nav.security.mock.oauth2.MockOAuth2Server
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@ContextConfiguration(initializers = [MockOAuth2ServerInitializer::class])
class SecurityIntegrationTest {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var mockOAuth2Server: MockOAuth2Server

    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setup() {
        webTestClient =
            WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
    }

    @Nested
    inner class OffentligeEndepunkter {
        @Test
        fun `skal tillate tilgang til health uten token`() {
            webTestClient
                .get()
                .uri("/internal/health")
                .exchange()
                .expectStatus()
                .isOk
        }

        @Test
        fun `skal tillate tilgang til swagger uten token`() {
            webTestClient
                .get()
                .uri("/swagger-ui/index.html")
                .exchange()
                .expectStatus()
                .isOk
        }
    }

    @Nested
    inner class StsEndepunkter {
        @Test
        fun `skal avvise feed uten token`() {
            webTestClient
                .get()
                .uri("/api/barnetrygd/v1/feed?sistLesteSekvensId=0")
                .exchange()
                .expectStatus()
                .isUnauthorized
        }

        @Test
        fun `skal akseptere STS-token på feed endpoint`() {
            val token = JwtTokenTestUtil.lagStsToken(mockOAuth2Server)

            val status =
                webTestClient
                    .get()
                    .uri("/api/barnetrygd/v1/feed?sistLesteSekvensId=0")
                    .header("Authorization", "Bearer $token")
                    .exchange()
                    .returnResult()
                    .status

            assertFalse(status.is4xxClientError, "status var $status, forventet ikke 4xx")
        }

        @Test
        fun `skal avvise Azure AD-token på STS endpoint`() {
            val token = JwtTokenTestUtil.lagAzureAdToken(mockOAuth2Server)

            webTestClient
                .get()
                .uri("/api/barnetrygd/v1/feed?sistLesteSekvensId=0")
                .header("Authorization", "Bearer $token")
                .exchange()
                .expectStatus()
                .isUnauthorized
        }
    }

    @Nested
    inner class AzureAdEndepunkter {
        @Test
        fun `skal avvise STS-token på Azure AD endpoint`() {
            val token = JwtTokenTestUtil.lagStsToken(mockOAuth2Server)

            webTestClient
                .get()
                .uri("/api/barnetrygd/v1/feedazure?sistLesteSekvensId=0")
                .header("Authorization", "Bearer $token")
                .exchange()
                .expectStatus()
                .isUnauthorized
        }

        @Test
        fun `skal akseptere Azure AD-token på feedazure endpoint`() {
            val token = JwtTokenTestUtil.lagAzureAdToken(mockOAuth2Server)

            val status =
                webTestClient
                    .get()
                    .uri("/api/barnetrygd/v1/feedazure?sistLesteSekvensId=0")
                    .header("Authorization", "Bearer $token")
                    .exchange()
                    .returnResult()
                    .status

            assertFalse(status.is4xxClientError, "status var $status, forventet ikke 4xx")
        }

        @Test
        fun `skal akseptere Azure AD-token på opprettet endpoint`() {
            val token = JwtTokenTestUtil.lagAzureAdToken(mockOAuth2Server)

            val status =
                webTestClient
                    .post()
                    .uri("/api/barnetrygd/v1/feed/BA_Foedsel_v1/opprettet")
                    .header("Authorization", "Bearer $token")
                    .bodyValue("12345678901")
                    .exchange()
                    .returnResult()
                    .status

            assertFalse(status.is4xxClientError, "status var $status, forventet ikke 4xx")
        }

        @Test
        fun `skal avvise foedselsmelding uten token`() {
            restTestClient
                .post()
                .uri("/api/barnetrygd/v1/feed/foedselsmelding")
                .header("Content-Type", "application/json")
                .body("""{"fnrBarn": "12345678901"}""")
                .exchange()
                .expectStatus()
                .isUnauthorized
        }

        @Test
        fun `skal akseptere Azure AD-token på foedselsmelding endpoint`() {
            val token = JwtTokenTestUtil.lagAzureAdToken(mockOAuth2Server)

            restTestClient
                .post()
                .uri("/api/barnetrygd/v1/feed/foedselsmelding")
                .header("Authorization", "Bearer $token")
                .header("Content-Type", "application/json")
                .body("""{"fnrBarn": "12345678901"}""")
                .exchange()
                .expectStatus()
                .isCreated
        }

        @Test
        fun `skal avvise STS-token på foedselsmelding endpoint`() {
            val token = JwtTokenTestUtil.lagStsToken(mockOAuth2Server)

            restTestClient
                .post()
                .uri("/api/barnetrygd/v1/feed/foedselsmelding")
                .header("Authorization", "Bearer $token")
                .header("Content-Type", "application/json")
                .body("""{"fnrBarn": "12345678901"}""")
                .exchange()
                .expectStatus()
                .isUnauthorized
        }

        @Test
        fun `skal avvise vedtaksmelding uten token`() {
            restTestClient
                .post()
                .uri("/api/barnetrygd/v1/feed/vedtaksmelding")
                .header("Content-Type", "application/json")
                .body("""{"fnrStoenadsmottaker": "12345678901", "datoStartNyBa": "2024-01-01"}""")
                .exchange()
                .expectStatus()
                .isUnauthorized
        }

        @Test
        fun `skal akseptere Azure AD-token på vedtaksmelding endpoint`() {
            val token = JwtTokenTestUtil.lagAzureAdToken(mockOAuth2Server)

            restTestClient
                .post()
                .uri("/api/barnetrygd/v1/feed/vedtaksmelding")
                .header("Authorization", "Bearer $token")
                .header("Content-Type", "application/json")
                .body("""{"fnrStoenadsmottaker": "12345678901", "datoStartNyBa": "2024-01-01"}""")
                .exchange()
                .expectStatus()
                .isCreated
        }

        @Test
        fun `skal avvise STS-token på vedtaksmelding endpoint`() {
            val token = JwtTokenTestUtil.lagStsToken(mockOAuth2Server)

            restTestClient
                .post()
                .uri("/api/barnetrygd/v1/feed/vedtaksmelding")
                .header("Authorization", "Bearer $token")
                .header("Content-Type", "application/json")
                .body("""{"fnrStoenadsmottaker": "12345678901", "datoStartNyBa": "2024-01-01"}""")
                .exchange()
                .expectStatus()
                .isUnauthorized
        }

        @Test
        fun `skal avvise startbehandlingsmelding uten token`() {
            restTestClient
                .post()
                .uri("/api/barnetrygd/v1/feed/startbehandlingsmelding")
                .header("Content-Type", "application/json")
                .body("""{"fnrStoenadsmottaker": "12345678901"}""")
                .exchange()
                .expectStatus()
                .isUnauthorized
        }

        @Test
        fun `skal akseptere Azure AD-token på startbehandlingsmelding endpoint`() {
            val token = JwtTokenTestUtil.lagAzureAdToken(mockOAuth2Server)

            restTestClient
                .post()
                .uri("/api/barnetrygd/v1/feed/startbehandlingsmelding")
                .header("Authorization", "Bearer $token")
                .header("Content-Type", "application/json")
                .body("""{"fnrStoenadsmottaker": "12345678901"}""")
                .exchange()
                .expectStatus()
                .isCreated
        }

        @Test
        fun `skal avvise STS-token på startbehandlingsmelding endpoint`() {
            val token = JwtTokenTestUtil.lagStsToken(mockOAuth2Server)

            restTestClient
                .post()
                .uri("/api/barnetrygd/v1/feed/startbehandlingsmelding")
                .header("Authorization", "Bearer $token")
                .header("Content-Type", "application/json")
                .body("""{"fnrStoenadsmottaker": "12345678901"}""")
                .exchange()
                .expectStatus()
                .isUnauthorized
        }
    }
}
