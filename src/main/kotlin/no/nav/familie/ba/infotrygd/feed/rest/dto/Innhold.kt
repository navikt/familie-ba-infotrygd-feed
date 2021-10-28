package no.nav.familie.ba.infotrygd.feed.rest.dto

import java.time.LocalDate

interface Innhold

data class InnholdVedtak(val datoStartNyBA : LocalDate, val fnrStoenadsmottaker: String) : Innhold

data class InnholdStartBehandling(val fnrStoenadsmottaker: String ) : Innhold

data class InnholdFødsel(val fnrBarn: String) : Innhold
