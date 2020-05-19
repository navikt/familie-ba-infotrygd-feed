package no.nav.familie.ba.infotrygd.feed.rest

import io.swagger.v3.oas.annotations.Parameter
import no.nav.familie.ba.infotrygd.feed.rest.dto.Type
import no.nav.familie.ba.infotrygd.feed.service.InfotrygdFeedService
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.context.annotation.Profile
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import javax.validation.constraints.Size

@RestController
@Profile("!prod")
@ProtectedWithClaims(issuer = "sts")
class TestDataController(val infotrygdFeedService: InfotrygdFeedService) {

    @PostMapping("/api/foedselsmelding")
    fun lagNyFoedselsMelding(@Parameter(description = "Fødselsnummer barn.", required = true, example = "12345678910")
                             @RequestParam("fnrBarn") fnrBarn: String,

                             @Parameter(description = "Fødselsnummer mor.", example = "12345678910")
                             @RequestParam("fnrMor", required = false) fnrMor: String?,

                             @Parameter(description = "Fødselsnummer far.", example = "12345678910")
                             @RequestParam("fnrfar", required = false) fnrFar: String?): ResponseEntity<String> =

            opprettFeed(type = Type.BA_Foedsel_v1, fnrBarn = fnrBarn, fnrFar = fnrFar, fnrMor = fnrMor)

    @PostMapping("/api/vedtakmelding")
    fun lagNyHenvendelsesMelding(@Parameter(description = "Fødselsnummer støneds mottaker.", required = true, example = "12345678910")
                                 @RequestParam("fnrStoenadsmottaker") fnrStoenadsmottaker: String,

                                 @Parameter(description = "Fødselsnummer far.", example = "12345678910")
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                 @RequestParam("datoStartNyBA") datoStartNyBA: LocalDate?,

                                 @Parameter(description = "Fødselsnummer mor.", example = "12345678910")
                                 @RequestParam("fnrMor", required = false) fnrMor: String?,

                                 @Parameter(description = "Fødselsnummer far.", example = "12345678910")
                                 @RequestParam("fnrfar", required = false) fnrFar: String?): ResponseEntity<String> =

            opprettFeed(type = Type.BA_Vedtak_v1, fnrStoenadsmottaker = fnrStoenadsmottaker, datoStartNyBA = datoStartNyBA, fnrFar = fnrFar, fnrMor = fnrMor)

    private fun opprettFeed(type: Type, fnrBarn: String? = null, fnrFar: String? = null, fnrMor: String? = null, fnrStoenadsmottaker: String? = null, datoStartNyBA: LocalDate? = null): ResponseEntity<String> {
        return Result.runCatching { infotrygdFeedService.opprettNyFeed(type = type, fnrBarn = fnrBarn, fnrFar = fnrFar, fnrMor = fnrMor, fnrStonadsmottaker = fnrStoenadsmottaker, datoStartNyBA = datoStartNyBA) }
                .fold(onSuccess = {
                    ResponseEntity.ok("Hendelse opprettet")
                }, onFailure = {
                    ResponseEntity.badRequest().body("Klarte ikke opprette meldinger basert på hendelse")
                })
    }
}