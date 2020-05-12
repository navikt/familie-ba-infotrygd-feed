package no.nav.familie.ba.infotrygd.feed.vedtak

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/vedtak")
class VedtakController {

    @GetMapping(path = ["/{vedtakId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun ping(@PathVariable vedtakId: String)
            : ResponseEntity<String> {
        return ResponseEntity.ok("ok")
    }
}
