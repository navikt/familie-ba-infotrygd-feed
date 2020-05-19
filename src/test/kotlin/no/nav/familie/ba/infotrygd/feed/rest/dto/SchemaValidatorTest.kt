package no.nav.familie.ba.infotrygd.feed.rest.dto

import com.fasterxml.jackson.databind.JsonNode
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
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

    @Test
    fun `Dto for fødsel med fnrMor og fnrFar validerer mot schema`() {
        val node = objectMapper.valueToTree<JsonNode>(testDtoForFødsel(fnrMor = "12345678910", fnrFar = "12345678910"))
        val feilListe = schema.validate(node)
        Assertions.assertTrue(feilListe.isEmpty())
    }

    private fun testDtoForFødsel(fnr: String = "12345678910", fnrMor: String? = null, fnrFar: String? = null): FeedMeldingDto {

        return FeedMeldingDto(
                tittel = "Feed schema validator test",
                inneholderFlereElementer = false,
                elementer = listOf(
                        FeedElement(
                                innhold = if( fnrMor != null && fnrFar != null) {
                                    InnholdFødsel(fnrBarn = fnr, fnrFar = fnrFar, fnrmor = fnrMor)
                                } else {
                                    InnholdFødsel(fnrBarn = fnr)
                                },
                                metadata = ElementMetadata(opprettetDato = LocalDateTime.now()),
                                sekvensId = 42,
                                type = Type.BA_Foedsel_v1
                        ))
        )
    }

    private fun testDtoForVedtak(fnrStoenadsmottaker: String =  "12345678910"): FeedMeldingDto {

        return FeedMeldingDto(
                tittel = "Feed schema validator test",
                inneholderFlereElementer = false,
                elementer = listOf(
                        FeedElement(
                                innhold = InnholdVedtak(datoStartNyBA = LocalDate.now(), fnrStoenadsmottaker = fnrStoenadsmottaker),
                                metadata = ElementMetadata(opprettetDato = LocalDateTime.now()),
                                sekvensId = 42,
                                type = Type.BA_Vedtak_v1
                        ))
        )
    }

    private val schema: JsonSchema
        get() {
            val schemaNode = objectMapper.readTree(hentFeedSchema())
            return JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(schemaNode)
        }

    private fun hentFeedSchema(): String {
        val inputStream = this::class.java.classLoader.getResourceAsStream("schema/barnetrygd-feed-schema.json")
        return String(inputStream!!.readAllBytes(), Charset.forName("UTF-8"))
    }
}