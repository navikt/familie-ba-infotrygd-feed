package no.nav.familie.ba.infotrygd.feed.rest.dto

import java.time.LocalDateTime

data class ElementMetadata(
    val opprettetDato: LocalDateTime // TODO: NB K9 bruker java.time.OffSetDateTime her
)
