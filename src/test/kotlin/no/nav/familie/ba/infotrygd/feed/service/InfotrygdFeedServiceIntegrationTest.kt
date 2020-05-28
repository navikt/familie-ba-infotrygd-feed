package no.nav.familie.ba.infotrygd.feed.service

import no.nav.familie.ba.infotrygd.feed.database.DbContainerInitializer
import no.nav.familie.ba.infotrygd.feed.rest.dto.Type
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
@ContextConfiguration(initializers = [DbContainerInitializer::class])
@ActiveProfiles("postgres")
@Tag("integration")
@Disabled("Midlertidig skrudd av")
class InfotrygdFeedServiceIntegrationTest {

    @Autowired
    lateinit var infotrygdFeedService: InfotrygdFeedService

    @Test
    fun `Hent feeds fra database`() {
        val fnrBarn = "12345678911"
        infotrygdFeedService.opprettNyFeed(type = Type.BA_Foedsel_v1, fnrBarn = fnrBarn)
        val feeds = infotrygdFeedService.hentMeldingerFraFeed(0)

        Assertions.assertNotNull(feeds.find { it.type == Type.BA_Foedsel_v1 && it.fnrBarn == fnrBarn })
    }

    @Test
    fun `Hent feed-meldinger med høy sistLestSekvensId gir tom liste`() {
        val feedListe = infotrygdFeedService.hentMeldingerFraFeed(1000)

        Assertions.assertTrue(feedListe.isEmpty())
    }
}