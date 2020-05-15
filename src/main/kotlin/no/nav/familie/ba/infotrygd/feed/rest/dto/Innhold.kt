package no.nav.familie.ba.infotrygd.feed.rest.dto

import java.time.LocalDate

data class Innhold(
        val datoStartNyBA: LocalDate? = null,
        val fnrBarn: String? = null,
        val fnrFar: String? = null,
        val fnrMor: String? = null,
        val fnrStoenadsmottaker: String? = null
)