package no.nav.familie.ba.infotrygd.feed.service

import no.nav.familie.ba.infotrygd.feed.database.DbContainerInitializer
import no.nav.familie.ba.infotrygd.feed.rest.dto.Type
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@SpringBootTest
@ExtendWith(SpringExtension::class)
@ContextConfiguration(initializers = [DbContainerInitializer::class])
@ActiveProfiles("postgres")
@Tag("integration")
class InfotrygdFeedServiceIntegrationTest {

    @Autowired
    lateinit var infotrygdFeedService: InfotrygdFeedService

    @Test
    fun `Hent feeds fra database`() {
        val fnrBarn = "12345678911"
        infotrygdFeedService.opprettNyFeed(type = Type.BA_Foedsel_v1, fnrBarn = fnrBarn)
        val feeds = infotrygdFeedService.hentMeldingerFraFeed(0)

        Assertions.assertNotNull(feeds.find { it.type == Type.BA_Foedsel_v1 && it.fnrBarn == fnrBarn && it.duplikat == false })
    }

    @Test
    fun `Verifiser at duplikat av fødselesmleding ikke hentes fra database`() {
        val fnrBarn = "12345678912"
        infotrygdFeedService.opprettNyFeed(type = Type.BA_Foedsel_v1, fnrBarn = fnrBarn)
        infotrygdFeedService.opprettNyFeed(type = Type.BA_Foedsel_v1, fnrBarn = fnrBarn)

        val feeds = infotrygdFeedService.hentMeldingerFraFeed(0)

        Assertions.assertEquals(1, feeds.filter { it.fnrBarn == fnrBarn }.size)
    }

    @Test
    fun `Verifiser at alle duplikat av vedtak hentes fra database`() {
        val fnrStonadsmottaker = "12345678913"
        infotrygdFeedService.opprettNyFeed(type = Type.BA_Vedtak_v1, datoStartNyBA = LocalDate.now(), fnrStonadsmottaker = fnrStonadsmottaker)
        infotrygdFeedService.opprettNyFeed(type = Type.BA_Vedtak_v1, datoStartNyBA = LocalDate.now(), fnrStonadsmottaker = fnrStonadsmottaker)

        val feeds = infotrygdFeedService.hentMeldingerFraFeed(0)

        Assertions.assertEquals(2, feeds.filter { it.fnrStonadsmottaker == fnrStonadsmottaker }.size)
    }

    @Test
    fun `Verifiser at maks definert antall feeds blir returnert`() {
        val fnrStonadsmottaker = "10000000000"
        for (i in 1..3) infotrygdFeedService.opprettNyFeed(type = Type.BA_Vedtak_v1, datoStartNyBA = LocalDate.now(), fnrStonadsmottaker = fnrStonadsmottaker + i)

        val feeds = infotrygdFeedService.hentMeldingerFraFeed(0, 2)

        Assertions.assertEquals(2, feeds.size)
    }

    @Test
    fun `Hent feed-meldinger med høy sistLestSekvensId gir tom liste`() {
        val feedListe = infotrygdFeedService.hentMeldingerFraFeed(1000)

        Assertions.assertTrue(feedListe.isEmpty())
    }

    @Test
    fun `Hent feed-meldinger av angitt type via fnr`() {
        val fnr = "12345678910"
        infotrygdFeedService.opprettNyFeed(type = Type.BA_Foedsel_v1, fnrBarn = fnr)
        infotrygdFeedService.opprettNyFeed(type = Type.BA_Vedtak_v1, fnrStonadsmottaker = fnr)

        Assertions.assertEquals(1, infotrygdFeedService.hentMeldingerFraFeed(fnr, Type.BA_Foedsel_v1).size)
        Assertions.assertEquals(1, infotrygdFeedService.hentMeldingerFraFeed(fnr, Type.BA_Vedtak_v1).size)
        Assertions.assertEquals(0, infotrygdFeedService.hentMeldingerFraFeed(fnr, Type.BA_StartBeh).size)
    }
}
