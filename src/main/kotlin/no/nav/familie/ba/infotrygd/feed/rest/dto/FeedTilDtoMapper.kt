package no.nav.familie.ba.infotrygd.feed.rest.dto

import no.nav.familie.ba.infotrygd.feed.database.Feed
import java.time.LocalDateTime


fun konverterTilFeedMeldingDto(feedListe: List<Feed>): FeedMeldingDto =
        FeedMeldingDto(
                tittel = "Barnetrygd feed",
                inneholderFlereElementer = feedListe.size > 1,
                elementer = feedListe.map {
                    FeedElement(
                            metadata = ElementMetadata(opprettetDato = LocalDateTime.now()),
                            innhold =
                            if (it.type == Type.BA_Vedtak_v1)
                                InnholdVedtak(datoStartNyBA = it.datoStartNyBa!!, fnrStoenadsmottaker = it.fnrStonadsmottaker!!, fnrFar = it.fnrFar, fnrmor = it.fnrMor)
                            else
                                InnholdFødsel(fnrBarn = it.fnrBarn!!, fnrFar = it.fnrFar, fnrmor = it.fnrMor),
                            sekvensId = it.sekvensId.toInt(),
                            type = it.type
                    )
                })
