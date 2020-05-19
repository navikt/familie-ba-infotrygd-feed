package no.nav.familie.ba.infotrygd.feed.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import no.nav.familie.ba.infotrygd.feed.rest.dto.FeedMeldingDto
import no.nav.familie.ba.infotrygd.feed.rest.dto.konverterTilFeedMeldingDto
import no.nav.familie.ba.infotrygd.feed.service.InfotrygdFeedService
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@ProtectedWithClaims(issuer = "sts")
class InfotrygdFeedController(private val infotrygdFeedService: InfotrygdFeedService) {

    @Operation(
            summary = "Hent liste med hendelser.",
            description = "Henter hendelser med sekvensId større enn sistLesteSekvensId."
    )
    @GetMapping("/api/v1/barnetrygd/feed", produces = ["application/json; charset=us-ascii"])
    fun feed(
            @Parameter(description = "Sist leste sekvensnummer.", required = true, example = "0")
            @RequestParam("sistLesteSekvensId") sekvensnummer: Long,

            @Parameter(description = "Maks antall elementer i svaret som kommer tilbake.", required = true, example = "100")
            @RequestParam("maxAntall", required = false, defaultValue = "100") maksAntall: Int
    ) : ResponseEntity<FeedMeldingDto> =
            Result.runCatching {
                konverterTilFeedMeldingDto(infotrygdFeedService.hentMeldingerFraFeed(sistLestSekvensId = sekvensnummer))
            }.fold(
                    onSuccess = { ResponseEntity.ok(it) },
                    onFailure = { ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() }
            )
}