package no.nav.familie.ba.infotrygd.feed.rest

import no.nav.familie.ba.infotrygd.feed.rest.dto.FødselsDto
import no.nav.familie.ba.infotrygd.feed.rest.dto.StartBehandlingDto
import no.nav.familie.ba.infotrygd.feed.rest.dto.Type
import no.nav.familie.ba.infotrygd.feed.rest.dto.VedtakDto
import no.nav.familie.ba.infotrygd.feed.service.InfotrygdFeedService
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@Profile("!prod")
@ProtectedWithClaims(issuer = "sts")
class TestDataController(
    val infotrygdFeedService: InfotrygdFeedService,
) {
    @PostMapping("/api/foedselsmelding", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun lagNyFødselsMelding(
        @RequestBody fødselsDto: FødselsDto,
    ): ResponseEntity<String> = opprettFeed(type = Type.BA_Foedsel_v1, fnrBarn = fødselsDto.fnrBarn)

    @PostMapping("/api/vedtakmelding")
    fun lagNyHenvendelsesMelding(
        @RequestBody vedtakDto: VedtakDto,
    ): ResponseEntity<String> =

        opprettFeed(
            type = Type.BA_Vedtak_v1,
            fnrStoenadsmottaker = vedtakDto.fnrStoenadsmottaker,
            datoStartNyBA = vedtakDto.datoStartNyBa,
        )

    @PostMapping(
        "/api/startbehandlingsmelding",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun lagNyStartBehandlingsMelding(
        @RequestBody startBehandlingDto: StartBehandlingDto,
    ): ResponseEntity<String> = opprettFeed(type = Type.BA_StartBeh, fnrStoenadsmottaker = startBehandlingDto.fnrStoenadsmottaker)

    private fun opprettFeed(
        type: Type,
        fnrBarn: String? = null,
        fnrStoenadsmottaker: String? = null,
        datoStartNyBA: LocalDate? = null,
    ): ResponseEntity<String> =
        Result
            .runCatching {
                infotrygdFeedService.opprettNyFeed(
                    type = type,
                    fnrBarn = fnrBarn,
                    fnrStonadsmottaker = fnrStoenadsmottaker,
                    datoStartNyBA = datoStartNyBA,
                )
            }.fold(onSuccess = {
                ResponseEntity.ok("Hendelse opprettet")
            }, onFailure = {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Klarte ikke opprette meldinger basert på hendelse")
            })
}
