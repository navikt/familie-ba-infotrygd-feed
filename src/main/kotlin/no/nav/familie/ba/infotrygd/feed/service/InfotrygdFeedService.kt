package no.nav.familie.ba.infotrygd.feed.service

import no.nav.familie.ba.infotrygd.feed.database.Feed
import no.nav.familie.ba.infotrygd.feed.database.FeedRepository
import no.nav.familie.ba.infotrygd.feed.rest.dto.Type
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class InfotrygdFeedService(val feedRepository: FeedRepository) {

    fun opprettNyFeed(type: Type,
            fnrBarn: String?,
            fnrFar: String?,
            fnrMor: String?,
            fnrStonadsmottaker: String?,
            datoStartNyBA: LocalDate?
    ) = feedRepository.save(
                    Feed(
                            type = type,
                            fnrBarn = fnrBarn,
                            fnrFar = fnrFar,
                            fnrMor = fnrMor,
                            fnrStonadsmottaker = fnrStonadsmottaker,
                            datoStartNyBa = datoStartNyBA
                    ))
}