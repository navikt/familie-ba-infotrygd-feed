package no.nav.familie.ba.infotrygd.feed.service

import no.nav.familie.ba.infotrygd.feed.database.Feed
import no.nav.familie.ba.infotrygd.feed.database.FeedRepository
import no.nav.familie.ba.infotrygd.feed.rest.dto.Type
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class InfotrygdFeedService(val feedRepository: FeedRepository) {

    fun opprettNyFeed(type: Type,
            fnrBarn: String? = null,
            fnrStonadsmottaker: String? = null,
            datoStartNyBA: LocalDate? = null
    ) = feedRepository.save(
                    Feed(
                            type = type,
                            fnrBarn = fnrBarn,
                            fnrStonadsmottaker = fnrStonadsmottaker,
                            datoStartNyBa = datoStartNyBA
                    ))

    fun hentMeldingerFraFeed(sistLestSekvensId: Long): List<Feed> =
            feedRepository.finnMeldingerMedSekvensIdStørreEnn(sistLestSekvensId)
}