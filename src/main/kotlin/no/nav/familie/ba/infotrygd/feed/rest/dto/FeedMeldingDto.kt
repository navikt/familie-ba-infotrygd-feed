package no.nav.familie.ba.infotrygd.feed.rest.dto

data class FeedMeldingDto(
    val elementer: List<FeedElement>,
    val inneholderFlereElementer: Boolean,
    val tittel: String
)
