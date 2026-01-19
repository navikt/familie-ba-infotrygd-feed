package no.nav.familie.ba.infotrygd.feed.rest.dto

import com.fasterxml.jackson.databind.JsonNode
import com.networknt.schema.Schema
import com.networknt.schema.SchemaRegistry
import com.networknt.schema.SpecificationVersion
import no.nav.familie.kontrakter.felles.objectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.LocalDateTime

class SchemaValidatorTest {
    @Test
    fun `Dto for fødsel validerer mot schema`() {
        val node = objectMapper.valueToTree<JsonNode>(testDtoForFødsel())
        val feilListe = schema.validate(node)
        Assertions.assertTrue(feilListe.isEmpty())
    }

    @Test
    fun `Dto for start behandling validerer mot schema`() {
        val node = objectMapper.valueToTree<JsonNode>(testDtoForStartBehandling())
        val feilListe = schema.validate(node)
        Assertions.assertTrue(feilListe.isEmpty())
    }

    @Test
    fun `Dto for vedtak validerer mot schema`() {
        val node = objectMapper.valueToTree<JsonNode>(testDtoForVedtak())
        val feilListe = schema.validate(node)
        Assertions.assertTrue(feilListe.isEmpty())
    }

    @Test
    fun `Dto for fødsel validerer ikke dersom fnrBarn har feil format`() {
        val node = objectMapper.valueToTree<JsonNode>(testDtoForFødsel("123456"))
        val feilListe = schema.validate(node)
        Assertions.assertEquals(1, feilListe.size)
    }

    @Test
    fun `Dto for vedtak validerer ikke dersom fnrStoenadsmottaker har feil format`() {
        val node = objectMapper.valueToTree<JsonNode>(testDtoForVedtak("123456"))
        val feilListe = schema.validate(node)
        Assertions.assertEquals(1, feilListe.size)
    }

    private fun testDtoForFødsel(fnr: String = "12345678910"): FeedMeldingDto =
        FeedMeldingDto(
            tittel = "Feed schema validator test",
            inneholderFlereElementer = false,
            elementer =
                listOf(
                    FeedElement(
                        InnholdFødsel(fnrBarn = fnr),
                        metadata = ElementMetadata(opprettetDato = LocalDateTime.now()),
                        sekvensId = 42,
                        type = Type.BA_Foedsel_v1,
                    ),
                ),
        )

    private fun testDtoForVedtak(fnrStoenadsmottaker: String = "12345678910"): FeedMeldingDto =
        FeedMeldingDto(
            tittel = "Feed schema validator test",
            inneholderFlereElementer = false,
            elementer =
                listOf(
                    FeedElement(
                        innhold = InnholdVedtak(datoStartNyBA = LocalDate.now(), fnrStoenadsmottaker = fnrStoenadsmottaker),
                        metadata = ElementMetadata(opprettetDato = LocalDateTime.now()),
                        sekvensId = 42,
                        type = Type.BA_Vedtak_v1,
                    ),
                ),
        )

    private fun testDtoForStartBehandling(fnrStoenadsmottaker: String = "12345678910"): FeedMeldingDto =
        FeedMeldingDto(
            tittel = "Feed schema validator test",
            inneholderFlereElementer = false,
            elementer =
                listOf(
                    FeedElement(
                        innhold = InnholdStartBehandling(fnrStoenadsmottaker = fnrStoenadsmottaker),
                        metadata = ElementMetadata(opprettetDato = LocalDateTime.now()),
                        sekvensId = 42,
                        type = Type.BA_StartBeh,
                    ),
                ),
        )

    private val schema: Schema
        get() {
            val schemaNode = objectMapper.readTree(hentFeedSchema())
            val schemaRegistry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_4)
            return schemaRegistry.getSchema(schemaNode)
        }

    private fun hentFeedSchema(): String {
        val inputStream = this::class.java.classLoader.getResourceAsStream("schema/barnetrygd-feed-schema.json")
        return String(inputStream!!.readAllBytes(), Charset.forName("UTF-8"))
    }
}
