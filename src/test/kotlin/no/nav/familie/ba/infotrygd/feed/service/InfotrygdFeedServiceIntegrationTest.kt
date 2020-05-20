package no.nav.familie.ba.infotrygd.feed.service

import junit.framework.Assert.assertEquals
import no.nav.familie.ba.infotrygd.feed.database.DbContainerInitializer
import no.nav.familie.ba.infotrygd.feed.rest.dto.Type
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
class InfotrygdFeedServiceIntegrationTest {

    @Autowired
    lateinit var infotrygdFeedService: InfotrygdFeedService

    @Test
    fun `Persister feeds i database`() {
        infotrygdFeedService.opprettNyFeed(type = Type.BA_Foedsel_v1, fnrBarn = "12345678910")
    }

    @Test
    fun `Hent feeds fra database`() {
        infotrygdFeedService.opprettNyFeed(type = Type.BA_Foedsel_v1, fnrBarn = "12345678910")
        val feeds = infotrygdFeedService.hentMeldingerFraFeed(0)

        assertEquals("", 1, feeds.size)
    }
}