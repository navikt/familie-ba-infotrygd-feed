package no.nav.familie.ba.infotrygd.feed.rest

import io.micrometer.core.annotation.Timed
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@ProtectedWithClaims(issuer = "sts")
class InfotrygdFeedController() {

    @Operation(
            summary = "Hent liste med hendelser.",
            description = "Henter hendelser med sekvensId større enn sistLesteSekvensId."
    )
    @GetMapping("/api/v1/barnetrygd/feed",
            produces = ["application/json; charset=us-ascii"])
    fun feed(
            @Parameter(description = "Sist leste sekvensnummer.", required = true, example = "0")
            @RequestParam("sistLesteSekvensId") sekvensnummer: Long,

            @Parameter(description = "Maks antall elementer i svaret som kommer tilbake.", required = true, example = "100")
            @RequestParam("maxAntall", required = false, defaultValue = "100") maksAntall: Int,

            @Parameter(description = "Støttede hendelser vedtak eller fødselsmeldinger.")
            @RequestParam("type") type: String,

            @Parameter(description = "Ikke implementert. Ta kontakt med Team Familie dersom dette er ønsket.")
            @RequestParam("aktoerId", required = false) aktørId: String?
    ) : ResponseEntity<String> {
        if(aktørId != null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Det er ikke implementert støtte for parametrene 'aktoerId' på gjeldende tidspunkt. " +
                            "Vennligst ta kontakt med Team Familie dersom det er behov for å implementere disse.")
        }

        return ResponseEntity.ok("ok")
    }
}