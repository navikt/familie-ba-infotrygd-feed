package no.nav.familie.ba.infotrygd.feed.rest.dto

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
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
    fun dtoForFødselValidererMotSchema() {
        val node = objectMapper.valueToTree<JsonNode>(testDtoForFødsel())
        val feilListe = schema.validate(node)
        Assertions.assertTrue(feilListe.isEmpty())
    }

    private fun testDtoForFødsel(): FeedMeldingDto {

        return FeedMeldingDto(
                tittel = "Feed schema validator test",
                inneholderFlereElementer = false,
                elementer = listOf(
                        FeedElement(
                                innhold = InnholdOpphort(fnrBarn = "12345678910"),
                                metadata = ElementMetadata(opprettetDato = LocalDateTime.now()),
                                sekvensId = 42,
                                type = Type.BA_Foedsel_v1
                        ))
        )
    }

    private fun testDtoForVedtak(): FeedMeldingDto {

        return FeedMeldingDto(
                tittel = "Feed schema validator test",
                inneholderFlereElementer = false,
                elementer = listOf(
                        FeedElement(
                                innhold = InnholdFoedsel(datoStartNyBA = LocalDate.now(), fnrStoenadsmottaker = "12345678910"),
                                metadata = ElementMetadata(opprettetDato = LocalDateTime.now()),
                                sekvensId = 42,
                                type = Type.BA_Opphoert_v1
                        ))
        )
    }

    private val schema: JsonSchema
        get() {
            val schemaNode = objectMapper.readTree(hentFeedSchema())
            disallowAdditionalProperties(schemaNode)
            // TODO: Denne klarer ikke hente schema med V7-versjonen. Går fint med default-versjon som er V4, men vi må ha V7
            return JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(schemaNode)
        }

    // Schemavalideringsbiblioteket har ikke støtte for å globalt validere at det ikke finnes ukjente properties
    // i JSON-objektet. Bruker derfor denne metoden til å modifisere schema slik at ukjente properties ikke er lov
    // når testen kjører.
    private fun disallowAdditionalProperties(node: JsonNode) {
        if(node.isObject) {
            val typeNode = node.get("type")
            if(typeNode != null && typeNode.textValue() == "object") {
                val objectNode = node as ObjectNode
                objectNode.put("additionalProperties", false)
            }

            for(field in node.fields()) {
                disallowAdditionalProperties(field.value)
            }
        }
        if(node.isArray) {
            for(elementNode in node.elements()) {
                disallowAdditionalProperties(elementNode)
            }
        }
    }

    private fun hentFeedSchema(): String {
        val inputStream = this::class.java.classLoader.getResourceAsStream("schema/barnetrygd-feed-schema.json")
        return String(inputStream!!.readAllBytes(), Charset.forName("UTF-8"))
    }
}