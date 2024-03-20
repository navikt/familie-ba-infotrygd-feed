package no.nav.familie.ba.infotrygd.feed.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import no.nav.familie.ba.infotrygd.feed.rest.dto.FeedMeldingDto
import no.nav.familie.ba.infotrygd.feed.rest.dto.FeedOpprettetDto
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
@ProtectedWithClaims(issuer = "sts")
class InfotrygdFeedController(private val infotrygdFeedService: InfotrygdFeedService) {
    @Operation(
        summary = "Hent liste med hendelser.",
        description = "Henter hendelser med sekvensId større enn sistLesteSekvensId.",
    )
    @GetMapping("/v1/feed", produces = ["application/json; charset=us-ascii"])
    fun feed(
        @Parameter(description = "Sist leste sekvensnummer.", required = true, example = "0")
        @RequestParam("sistLesteSekvensId")
        sekvensnummer: Long,
    ): ResponseEntity<FeedMeldingDto> =
        Result.runCatching {
            konverterTilFeedMeldingDto(infotrygdFeedService.hentMeldingerFraFeed(sistLestSekvensId = sekvensnummer))
        }.fold(
            onSuccess = {
                log.info("Hentet ${it.elementer.size} feeds fra sekvensnummer $sekvensnummer")

                ResponseEntity.ok(it)
            },
            onFailure = {
                log.error("Feil ved henting av feeds fra sekvensnummer $sekvensnummer", it)

                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
            },
        )

    @Operation(
        summary = "Hent liste med hendelser.",
        description = "Henter hendelser med sekvensId større enn sistLesteSekvensId.",
    )
    @GetMapping("/v1/feedazure", produces = ["application/json; charset=us-ascii"])
    @ProtectedWithClaims(issuer = "azuread")
    fun feedAzure(
        @Parameter(description = "Sist leste sekvensnummer.", required = true, example = "0")
        @RequestParam("sistLesteSekvensId")
        sekvensnummer: Long,
    ): ResponseEntity<FeedMeldingDto> =
        Result.runCatching {
            konverterTilFeedMeldingDto(infotrygdFeedService.hentMeldingerFraFeed(sistLestSekvensId = sekvensnummer))
        }.fold(
            onSuccess = {
                log.info("Hentet ${it.elementer.size} feeds fra sekvensnummer $sekvensnummer")

                ResponseEntity.ok(it)
            },
            onFailure = {
                log.error("Feil ved henting av feeds fra sekvensnummer $sekvensnummer", it)

                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
            },
        )

    @PostMapping("/v1/feed/{type}/opprettet", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ProtectedWithClaims(issuer = "azuread")
    fun feedOpprettet(
        @PathVariable type: Type,
        @RequestBody fnr: String,
    ): ResponseEntity<Ressurs<List<FeedOpprettetDto>>> {
        return Result.runCatching {
            infotrygdFeedService.hentMeldingerFraFeed(fnr, type).map { FeedOpprettetDto(it.opprettetDato, it.datoStartNyBa) }
        }.fold(
            onSuccess = {
                secureLogger.info("Fant ${it.size} feeds av type ${type.name} opprettet for fnr $fnr", it)
                ResponseEntity.ok(Ressurs.success(it))
            },
            onFailure = {
                secureLogger.error("Feil ved verifisering av ${type.name} feed for fnr $fnr", it)
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
            },
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
        private val secureLogger = LoggerFactory.getLogger("secureLogger")
    }
}
