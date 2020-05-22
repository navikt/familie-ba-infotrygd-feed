package no.nav.familie.ba.infotrygd.feed.rest.dto

import java.time.LocalDate

data class TestDtoFødsel(val fnrBarn: String, val fnrFar: String?, val fnrMor: String?)

data class TestDtoVedtak(val datoStartNyBa: LocalDate, val fnrStoenadsmottaker: String, val fnrFar: String?, val fnrMor: String?)