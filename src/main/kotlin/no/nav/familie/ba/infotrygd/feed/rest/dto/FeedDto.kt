package no.nav.familie.ba.infotrygd.feed.rest.dto

import java.time.LocalDate

data class FødselsDto(val fnrBarn: String)

data class TestDtoVedtak(val datoStartNyBa: LocalDate, val fnrStoenadsmottaker: String)