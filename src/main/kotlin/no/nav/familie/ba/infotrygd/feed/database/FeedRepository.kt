package no.nav.familie.ba.infotrygd.feed.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FeedRepository : JpaRepository<Feed, Long> {

    @Query(value = "SELECT f FROM Feed f WHERE f.sekvensId > :sistLesteSekvensId ORDER BY f.sekvensId asc")
    fun finnMeldingerMedSekvensIdStørreEnn(sistLesteSekvensId: Long): List<Feed>
}