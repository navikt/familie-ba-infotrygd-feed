package no.nav.familie.ba.infotrygd.feed.rest

import no.nav.familie.ba.infotrygd.feed.rest.dto.TestDtoFødsel
import no.nav.familie.ba.infotrygd.feed.rest.dto.TestDtoVedtak
import no.nav.familie.ba.infotrygd.feed.rest.dto.Type
import no.nav.familie.ba.infotrygd.feed.service.InfotrygdFeedService
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@Profile("!prod")
@ProtectedWithClaims(issuer = "sts")
class TestDataController(val infotrygdFeedService: InfotrygdFeedService) {

    @PostMapping("/api/foedselsmelding", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun lagNyFødselsMelding(@RequestBody testDtoFødsel: TestDtoFødsel): ResponseEntity<String> =
            opprettFeed(type = Type.BA_Foedsel_v1, fnrBarn = testDtoFødsel.fnrBarn, fnrFar = testDtoFødsel.fnrFar, fnrMor = testDtoFødsel.fnrMor)

    @PostMapping("/api/vedtakmelding")
    fun lagNyHenvendelsesMelding(@RequestBody testDtoVedtak: TestDtoVedtak): ResponseEntity<String> =

            opprettFeed(type = Type.BA_Vedtak_v1,
                    fnrStoenadsmottaker = testDtoVedtak.fnrStoenadsmottaker,
                    datoStartNyBA = testDtoVedtak.datoStartNyBa,
                    fnrFar = testDtoVedtak.fnrFar,
                    fnrMor = testDtoVedtak.fnrMor
            )

    private fun opprettFeed(type: Type,
                            fnrBarn: String? = null,
                            fnrFar: String? = null,
                            fnrMor: String? = null,
                            fnrStoenadsmottaker: String? = null,
                            datoStartNyBA: LocalDate? = null): ResponseEntity<String> {
        return Result.runCatching { infotrygdFeedService.opprettNyFeed(type = type, fnrBarn = fnrBarn, fnrFar = fnrFar, fnrMor = fnrMor, fnrStonadsmottaker = fnrStoenadsmottaker, datoStartNyBA = datoStartNyBA) }
                .fold(onSuccess = {
                    ResponseEntity.ok("Hendelse opprettet")
                }, onFailure = {
                    ResponseEntity.badRequest().body("Klarte ikke opprette meldinger basert på hendelse")
                })
    }
}