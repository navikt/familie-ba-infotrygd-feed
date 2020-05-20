package no.nav.familie.ba.infotrygd.feed.database

import no.nav.familie.ba.infotrygd.feed.rest.dto.Type
import java.time.LocalDate
import javax.persistence.*

@Entity(name = "Feed")
@Table(name = "FEED")
data class Feed(
        @Id
        @Column(name = "sekvens_id")
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feed_seq_generator")
        @SequenceGenerator(name = "feed_seq_generator", sequenceName = "feed_seq", allocationSize = 50)
        val sekvensId: Long = 0,

        @Enumerated(EnumType.STRING)
        @Column(name = "type", nullable = false)
        val type: Type,

        @Column(name = "fnr_stonadsmottaker", nullable = true)
        var fnrStonadsmottaker: String? = null,

        @Column(name = "dato_start_ny_ba", nullable = true)
        var datoStartNyBa: LocalDate? = null,

        @Column(name = "fnr_barn", nullable = true)
        var fnrBarn: String? = null,

        @Column(name = "fnr_mor", nullable = true)
        var fnrMor: String? = null,

        @Column(name = "fnr_far", nullable = true)
        var fnrFar: String? = null
        ) {

        override fun toString(): String {
                return "Feed(sekvensId=$sekvensId, type=$type, datoStartNyBa=$datoStartNyBa)"
        }
}

