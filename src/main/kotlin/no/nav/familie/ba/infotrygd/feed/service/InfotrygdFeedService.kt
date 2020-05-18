package no.nav.familie.ba.infotrygd.feed.service

import no.nav.familie.ba.infotrygd.feed.database.Feed
import no.nav.familie.ba.infotrygd.feed.database.FeedRepository
import no.nav.familie.ba.infotrygd.feed.rest.dto.FeedMeldingDto
import no.nav.familie.ba.infotrygd.feed.rest.dto.InnholdFødsel
import no.nav.familie.ba.infotrygd.feed.rest.dto.InnholdVedtak
import no.nav.familie.ba.infotrygd.feed.rest.dto.Type
import org.springframework.stereotype.Service

@Service
class InfotrygdFeedService(val feedRepository: FeedRepository) {

    fun opprettNyHendelse(hendelse: FeedMeldingDto) {

        val vedtakMeldinger = hendelse.elementer
                .filter { it.type == Type.BA_Opphoert_v1 }
                .map {
                    Feed(
                            type = Type.BA_Opphoert_v1,
                            datoStartNyBa = (it.innhold as InnholdVedtak).datoStartNyBA,
                            fnrStonadsmottaker = it.innhold.fnrStoenadsmottaker
                    )
                }

        val fødselMeldinger = hendelse.elementer
                .filter { it.type == Type.BA_Foedsel_v1 }
                .map {
                    Feed(
                            type = Type.BA_Foedsel_v1,
                            fnrBarn = (it.innhold as InnholdFødsel).fnrBarn
                    )
                }

        feedRepository.saveAll(vedtakMeldinger.plus(fødselMeldinger))
    }
}