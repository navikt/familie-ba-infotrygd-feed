package no.nav.familie.ba.infotrygd.feed.service

import no.nav.familie.ba.infotrygd.feed.database.Feed
import no.nav.familie.ba.infotrygd.feed.database.FeedRepository
import no.nav.familie.ba.infotrygd.feed.rest.dto.Type
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class InfotrygdFeedService(val feedRepository: FeedRepository) {

    fun opprettNyFeed(type: Type,
            fnrBarn: String? = null,
            fnrStonadsmottaker: String? = null,
            datoStartNyBA: LocalDate? = null
    ) {
        val erDuplikat = type.takeIf { it == Type.BA_Foedsel_v1 }
                ?.let { feedRepository.erDuplikatFoedselsmelding(type, fnrBarn!!) }
                ?: false

        feedRepository.save(
                Feed(
                        type = type,
                        fnrBarn = fnrBarn,
                        fnrStonadsmottaker = fnrStonadsmottaker,
                        datoStartNyBa = datoStartNyBA,
                        duplikat = erDuplikat,
                        opprettetDato = LocalDateTime.now()
                ))
    }

    fun hentVedtaksmeldingerFraFeed(sistLestSekvensId: Long, maxSize: Int = 100): List<Feed> =
            feedRepository.finnMeldingerMedSekvensIdStørreEnn(PageRequest.of(0, maxSize), sistLestSekvensId)

    fun hentMeldingerFraFeed(fnr: String, type: Type): List<Feed> {
        return feedRepository.finnMeldingerForFnr(fnr).filter { it.type == type }
    }
}