package no.nav.familie.ba.infotrygd.feed.rest.dto

import java.time.LocalDate

data class Innhold(
        val datoStartNyBA: LocalDate,
        val fnrBarn: String,
        val fnrFar: String,
        val fnrMor: String,
        val fnrStoenadsmottaker: String
)