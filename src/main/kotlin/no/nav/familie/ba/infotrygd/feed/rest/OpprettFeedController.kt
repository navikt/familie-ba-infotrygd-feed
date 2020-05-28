package no.nav.familie.ba.infotrygd.feed.rest

import no.nav.familie.ba.infotrygd.feed.rest.dto.FødselsDto
import no.nav.familie.ba.infotrygd.feed.rest.dto.Type
import no.nav.familie.ba.infotrygd.feed.service.InfotrygdFeedService
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController()
@RequestMapping("/api/v1/feed")
@ProtectedWithClaims(issuer = "azuread")
class OpprettFeedController(private val infotrygdFeedService: InfotrygdFeedService) {

    @PostMapping("/foedselsmelding", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun lagNyFødselsMelding(@RequestBody fødselsDto: FødselsDto): ResponseEntity<String> {
        return opprettFeed(type = Type.BA_Foedsel_v1, fnrBarn = fødselsDto.fnrBarn)
    }

    private fun opprettFeed(type: Type,
                            fnrBarn: String? = null,
                            fnrStoenadsmottaker: String? = null,
                            datoStartNyBA: LocalDate? = null): ResponseEntity<String> {
        return Result.runCatching { infotrygdFeedService.opprettNyFeed(type = type, fnrBarn = fnrBarn, fnrStonadsmottaker = fnrStoenadsmottaker, datoStartNyBA = datoStartNyBA) }
                .fold(onSuccess = {
                    ResponseEntity.ok("Hendelse opprettet")
                }, onFailure = {
                    ResponseEntity.badRequest().body("Klarte ikke opprette meldinger basert på hendelse")
                })
    }
}