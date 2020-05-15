package no.nav.familie.ba.infotrygd.feed.rest.dto

data class Elementer(
    val innhold: Innhold,
    val metadata: Metadata,
    val sekvensId: Int,
    val type: Type
)