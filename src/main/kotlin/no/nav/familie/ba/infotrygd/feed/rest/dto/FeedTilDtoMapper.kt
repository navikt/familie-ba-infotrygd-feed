package no.nav.familie.ba.infotrygd.feed.rest.dto

import no.nav.familie.ba.infotrygd.feed.database.Feed


fun konverterTilFeedMeldingDto(feedListe: List<Feed>): FeedMeldingDto =
        FeedMeldingDto(
                tittel = "Barnetrygd feed",
                inneholderFlereElementer = feedListe.size > 1,
                elementer = feedListe.map {
                    FeedElement(
                            metadata = ElementMetadata(opprettetDato = it.opprettetDato),
                            innhold =
                            if (it.type == Type.BA_Vedtak_v1)
                                InnholdVedtak(datoStartNyBA = it.datoStartNyBa!!, fnrStoenadsmottaker = it.fnrStonadsmottaker!!)
                            else if (it.type == Type.BA_StartBeh)
                                InnholdStartBehandling(fnrStoenadsmottaker = it.fnrStonadsmottaker!!)
                            else
                                InnholdFødsel(fnrBarn = it.fnrBarn!!),
                            sekvensId = it.sekvensId.toInt(),
                            type = it.type
                    )
                })
