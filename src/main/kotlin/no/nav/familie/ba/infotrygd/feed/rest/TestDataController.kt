package no.nav.familie.ba.infotrygd.feed.rest

import no.nav.familie.ba.infotrygd.feed.rest.dto.FeedMeldingDto
import no.nav.familie.ba.infotrygd.feed.service.InfotrygdFeedService
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@Profile("!prod")
@ProtectedWithClaims(issuer = "sts")
class TestDataController(val infotrygdFeedService: InfotrygdFeedService) {

    @PostMapping("/api/hendelse", consumes = ["application/json; charset=us-ascii"])
    fun lagNyHendelse(@RequestBody hendelse: FeedMeldingDto): ResponseEntity<String> {

        return Result.runCatching { infotrygdFeedService.opprettNyHendelse(hendelse) }
                .fold(onSuccess = {
                    ResponseEntity.ok("Hendelse opprettet")
                }, onFailure = {
                    ResponseEntity.badRequest().body("Klarte ikke opprette meldinger basert på hendelse")
                })
    }
}