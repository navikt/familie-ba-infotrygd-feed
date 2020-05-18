package no.nav.familie.ba.infotrygd.feed.rest.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FeedRepository : JpaRepository<Feed, Long> {
    @Query(value = "SELECT v FROM Feed v JOIN v.behandling b WHERE b.id = :behandlingId")
    fun finnVedtakForBehandling(behandlingId: Long): List<Feed>
}