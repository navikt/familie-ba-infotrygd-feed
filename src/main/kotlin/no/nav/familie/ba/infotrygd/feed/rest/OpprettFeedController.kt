package no.nav.familie.ba.infotrygd.feed.rest

import no.nav.familie.ba.infotrygd.feed.rest.dto.FødselsDto
import no.nav.familie.ba.infotrygd.feed.rest.dto.Type
import no.nav.familie.ba.infotrygd.feed.service.InfotrygdFeedService
import no.nav.familie.kontrakter.felles.Ressurs
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController()
@RequestMapping("/api/barnetrygd")
@ProtectedWithClaims(issuer = "azuread")
class OpprettFeedController(private val infotrygdFeedService: InfotrygdFeedService) {

    @PostMapping("/v1/feed/foedselsmelding", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun lagNyFødselsMelding(@RequestBody fødselsDto: FødselsDto): ResponseEntity<Ressurs<String>> {
        return opprettFeed(type = Type.BA_Foedsel_v1, fnrBarn = fødselsDto.fnrBarn)
    }

    private fun opprettFeed(type: Type,
                            fnrBarn: String? = null,
                            fnrStoenadsmottaker: String? = null,
                            datoStartNyBA: LocalDate? = null): ResponseEntity<Ressurs<String>> {
        return Result.runCatching { infotrygdFeedService.opprettNyFeed(type = type, fnrBarn = fnrBarn, fnrStonadsmottaker = fnrStoenadsmottaker, datoStartNyBA = datoStartNyBA) }
                .fold(onSuccess = {
                    ResponseEntity.status(HttpStatus.CREATED).body(Ressurs.success(data = "Create"))
                }, onFailure = {
                    secureLogger.error("Feil ved oppretting av fødselsmelding $fnrBarn.", it)
                    log.error("Feil ved oppretting av fødselsmelding", it)

                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Ressurs.failure("Klarte ikke opprette meldinger."))
                })
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
        private val secureLogger = LoggerFactory.getLogger("secureLogger")
    }
}