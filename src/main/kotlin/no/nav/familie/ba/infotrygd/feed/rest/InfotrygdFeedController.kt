package no.nav.familie.ba.infotrygd.feed.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import no.nav.familie.ba.infotrygd.feed.rest.dto.FeedMeldingDto
import no.nav.familie.ba.infotrygd.feed.rest.dto.OpprettetDto
import no.nav.familie.ba.infotrygd.feed.rest.dto.Type
import no.nav.familie.ba.infotrygd.feed.rest.dto.konverterTilFeedMeldingDto
import no.nav.familie.ba.infotrygd.feed.service.InfotrygdFeedService
import no.nav.familie.kontrakter.felles.Ressurs
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/barnetrygd")
class InfotrygdFeedController(private val infotrygdFeedService: InfotrygdFeedService) {

    @Operation(
            summary = "Hent liste med hendelser.",
            description = "Henter hendelser med sekvensId større enn sistLesteSekvensId."
    )
    @GetMapping("/v1/feed", produces = ["application/json; charset=us-ascii"])
    @ProtectedWithClaims(issuer = "sts")
    fun feed(
            @Parameter(description = "Sist leste sekvensnummer.", required = true, example = "0")
            @RequestParam("sistLesteSekvensId") sekvensnummer: Long
    ): ResponseEntity<FeedMeldingDto> =
            Result.runCatching {
                konverterTilFeedMeldingDto(infotrygdFeedService.hentVedtaksmeldingerFraFeed(sistLestSekvensId = sekvensnummer))
            }.fold(
                    onSuccess = {
                        log.info("Hentet ${it.elementer.size} feeds fra sekvensnummer $sekvensnummer")

                        ResponseEntity.ok(it)
                    },
                    onFailure = {
                        log.error("Feil ved henting av feeds fra sekvensnummer $sekvensnummer", it)

                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
                    }
            )

    @PostMapping("/v1/feed/{type}/opprettet", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ProtectedWithClaims(issuer = "azuread")
    fun feedOpprettet(@PathVariable(required = false) type: Type = Type.BA_Vedtak_v1,
                      @RequestBody fnr: String): ResponseEntity<Ressurs<OpprettetDto?>> {
        return Result.runCatching {
            infotrygdFeedService.hentMeldingerFraFeed(fnr, type).maxByOrNull { it.opprettetDato }
                ?.let { OpprettetDto(it.opprettetDato, it.datoStartNyBa) }
        }.fold(
            onSuccess = {
                ResponseEntity.ok(Ressurs.success(it))
            },
            onFailure = {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
            }
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
        private val secureLogger = LoggerFactory.getLogger("secureLogger")
    }
}