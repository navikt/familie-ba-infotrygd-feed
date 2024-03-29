package no.nav.familie.ba.infotrygd.feed.database

import no.nav.familie.ba.infotrygd.feed.rest.dto.Type
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FeedRepository : JpaRepository<Feed, Long> {
    @Query(value = "SELECT f FROM Feed f WHERE f.sekvensId > :sistLesteSekvensId AND f.duplikat = false ORDER BY f.sekvensId asc")
    fun finnMeldingerMedSekvensIdStørreEnn(
        pageable: Pageable,
        sistLesteSekvensId: Long,
    ): List<Feed>

    @Query(value = "SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false end FROM Feed f WHERE f.type = :type AND f.fnrBarn = :fnrBarn")
    fun erDuplikatFoedselsmelding(
        type: Type,
        fnrBarn: String,
    ): Boolean

    @Query(value = "SELECT f FROM Feed f WHERE f.fnrStonadsmottaker = :fnr OR f.fnrBarn = :fnr")
    fun finnMeldingerForFnr(fnr: String): List<Feed>
}
