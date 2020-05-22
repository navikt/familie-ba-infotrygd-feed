package no.nav.familie.ba.infotrygd.feed.rest.dto

import java.time.LocalDate

data class TestDtoFødsel(val fnrBarn: String)

data class TestDtoVedtak(val datoStartNyBa: LocalDate, val fnrStoenadsmottaker: String)