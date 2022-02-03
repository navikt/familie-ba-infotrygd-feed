package no.nav.familie.ba.infotrygd.feed.rest.dto

import java.time.LocalDate
import java.time.LocalDateTime

data class FødselsDto(val fnrBarn: String)

data class VedtakDto(val datoStartNyBa: LocalDate, val fnrStoenadsmottaker: String)

data class StartBehandlingDto(val fnrStoenadsmottaker: String)

data class FeedOpprettetDto(val opprettetDato: LocalDateTime, val datoStartNyBa: LocalDate?)