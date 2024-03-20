package no.nav.familie.ba.infotrygd.feed.rest.dto

import com.fasterxml.jackson.databind.JsonNode
import com.networknt.schema.JsonMetaSchema
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.NonValidationKeyword
import com.networknt.schema.SpecVersion
import com.networknt.schema.ValidatorTypeCode
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

    private fun testDtoForFødsel(fnr: String = "12345678910"): FeedMeldingDto {
        return FeedMeldingDto(
            tittel = "Feed schema validator test",
            inneholderFlereElementer = false,
            elementer = listOf(
                FeedElement(
                    InnholdFødsel(fnrBarn = fnr),
                    metadata = ElementMetadata(opprettetDato = LocalDateTime.now()),
                    sekvensId = 42,
                    type = Type.BA_Foedsel_v1
                )
            )
        )
    }

    private fun testDtoForVedtak(fnrStoenadsmottaker: String = "12345678910"): FeedMeldingDto {
        return FeedMeldingDto(
            tittel = "Feed schema validator test",
            inneholderFlereElementer = false,
            elementer = listOf(
                FeedElement(
                    innhold = InnholdVedtak(datoStartNyBA = LocalDate.now(), fnrStoenadsmottaker = fnrStoenadsmottaker),
                    metadata = ElementMetadata(opprettetDato = LocalDateTime.now()),
                    sekvensId = 42,
                    type = Type.BA_Vedtak_v1
                )
            )
        )
    }

    private fun testDtoForStartBehandling(fnrStoenadsmottaker: String = "12345678910"): FeedMeldingDto {
        return FeedMeldingDto(
            tittel = "Feed schema validator test",
            inneholderFlereElementer = false,
            elementer = listOf(
                FeedElement(
                    innhold = InnholdStartBehandling(fnrStoenadsmottaker = fnrStoenadsmottaker),
                    metadata = ElementMetadata(opprettetDato = LocalDateTime.now()),
                    sekvensId = 42,
                    type = Type.BA_StartBeh
                )
            )
        )
    }

    private val schema: JsonSchema
        get() {
            val schemaNode = objectMapper.readTree(hentFeedSchema())

            val URI = "https://json-schema.org/draft-04/schema"
            val ID = "\$id"
            val myJsonMetaSchema = JsonMetaSchema.Builder(URI)
                .idKeyword(ID)
                .keywords(ValidatorTypeCode.getKeywords(SpecVersion.VersionFlag.V4)) // keywords that may validly exist, but have no validation aspect to them
                .keywords(
                    listOf(
                        NonValidationKeyword("\$schema"),
                        NonValidationKeyword("\$id"),
                        NonValidationKeyword("examples")
                    )
                )
                .build()

            return JsonSchemaFactory.builder(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4)).defaultMetaSchemaIri(URI).metaSchema(myJsonMetaSchema).build()
                .getSchema(schemaNode)
        }

    private fun hentFeedSchema(): String {
        val inputStream = this::class.java.classLoader.getResourceAsStream("schema/barnetrygd-feed-schema.json")
        return String(inputStream!!.readAllBytes(), Charset.forName("UTF-8"))
    }
}
