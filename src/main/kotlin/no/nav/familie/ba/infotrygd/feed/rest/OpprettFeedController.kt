package no.nav.familie.ba.infotrygd.feed.rest

import no.nav.familie.ba.infotrygd.feed.rest.dto.FødselsDto
import no.nav.familie.ba.infotrygd.feed.rest.dto.StartBehandlingDto
import no.nav.familie.ba.infotrygd.feed.rest.dto.Type
import no.nav.familie.ba.infotrygd.feed.rest.dto.VedtakDto
import no.nav.familie.ba.infotrygd.feed.rest.dto.erAlfanummerisk
import no.nav.familie.ba.infotrygd.feed.service.InfotrygdFeedService
import no.nav.familie.kontrakter.felles.Ressurs
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController()
@RequestMapping("/api/barnetrygd")
@ProtectedWithClaims(issuer = "azuread")
class OpprettFeedController(
    private val infotrygdFeedService: InfotrygdFeedService,
) {
    @PostMapping(
        "/v1/feed/foedselsmelding",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun lagNyFødselsMelding(
        @RequestBody fødselsDto: FødselsDto,
    ): ResponseEntity<Ressurs<String>> {
        if (!fødselsDto.fnrBarn.erAlfanummerisk()) {
            error("fnrBarn er ikke alfanummerisk")
        }
        return opprettFeed(type = Type.BA_Foedsel_v1, fnrBarn = fødselsDto.fnrBarn)
    }

    @PostMapping(
        "/v1/feed/vedtaksmelding",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun lagNyVedtaksMelding(
        @RequestBody vedtakDto: VedtakDto,
    ): ResponseEntity<Ressurs<String>> {
        if (!vedtakDto.fnrStoenadsmottaker.erAlfanummerisk()) {
            error("fnrStoenadsmottaker er ikke alfanummerisk")
        }
        return opprettFeed(
            type = Type.BA_Vedtak_v1,
            fnrStoenadsmottaker = vedtakDto.fnrStoenadsmottaker,
            datoStartNyBA = vedtakDto.datoStartNyBa,
        )
    }

    @PostMapping(
        "/v1/feed/startbehandlingsmelding",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun lagNyStartBehandlingsMelding(
        @RequestBody startBehandlingDto: StartBehandlingDto,
    ): ResponseEntity<Ressurs<String>> {
        if (!startBehandlingDto.fnrStoenadsmottaker.erAlfanummerisk()) {
            error("fnrStoenadsmottaker er ikke alfanummerisk")
        }
        return opprettFeed(type = Type.BA_StartBeh, fnrStoenadsmottaker = startBehandlingDto.fnrStoenadsmottaker)
    }

    private fun opprettFeed(
        type: Type,
        fnrBarn: String? = null,
        fnrStoenadsmottaker: String? = null,
        datoStartNyBA: LocalDate? = null,
    ): ResponseEntity<Ressurs<String>> =
        Result
            .runCatching {
                infotrygdFeedService.opprettNyFeed(
                    type = type,
                    fnrBarn = fnrBarn,
                    fnrStonadsmottaker = fnrStoenadsmottaker,
                    datoStartNyBA = datoStartNyBA,
                )
            }.fold(onSuccess = {
                ResponseEntity.status(HttpStatus.CREATED).body(Ressurs.success(data = "Create"))
            }, onFailure = {
                secureLogger.error("Feil ved oppretting av feed $fnrBarn, $fnrStoenadsmottaker.", it)
                log.error("Feil ved oppretting av feed", it)

                ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Ressurs.failure("Klarte ikke opprette meldinger."))
            })

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
        private val secureLogger = LoggerFactory.getLogger("secureLogger")
    }
}
