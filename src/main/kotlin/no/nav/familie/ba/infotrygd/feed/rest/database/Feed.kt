package no.nav.familie.ba.infotrygd.feed.rest.database

import java.time.LocalDate
import javax.persistence.*

@Entity(name = "Feed")
@Table(name = "FEED")
data class Feed(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feed_seq_generator")
        @SequenceGenerator(name = "vedtak_seq_generator", sequenceName = "vedtak_seq", allocationSize = 50)
        val sekvensId: Long = 0,

       @Column(name = "type", nullable = false)
        val type: String,

        @Column(name = "fnr_stonadsmottaker", nullable = true)
        var fnrStonadsmottaker: String? = null,

        @Column(name = "dato_start_ny_ba", nullable = true)
        var datoStartNyBa: LocalDate,

        @Column(name = "fnr_barn", nullable = true)
        var fnrBarn: String? = null,

        @Column(name = "fnr_mor", nullable = true)
        var fnrMor: String? = null,

        @Column(name = "fnr_far", nullable = true)
        var fnrFar: String? = null

        // TODO: Skal denne spares i databasen eller genereres når den hentes?
        //@Column(name = "opprettet_dato", nullable = true)
        //var opprettetDato: String? = null,

        ) {

        override fun toString(): String {
                return "Vedtak(sekvensId=$sekvensId, type=$type, fnrStonadsmottaker='$fnrStonadsmottaker', " +
                        "datoStartNyBa=$datoStartNyBa, fnrBarn=$fnrBarn, fnrMor=$fnrMor, fnrFar=$fnrFar)"
        }
}

