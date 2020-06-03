package no.nicolasyanncouturier.obd

import no.nicolasyanncouturier.gbfs.StationInformation
import no.nicolasyanncouturier.gbfs.StationStatus
import org.hamcrest.CoreMatchers.containsString
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.Instant


@RunWith(SpringRunner::class)
@WebMvcTest(StatusApiController::class)
@AutoConfigureRestDocs(outputDir = "target/snippets", uriPort = 8089)
class StatusApiControllerTest {

    @Autowired
    private val mockMvc: MockMvc? = null

    @MockBean
    private val osloBysykkelService: OsloBysykkelService? = null

    @Test
    @Throws(Exception::class)
    fun shouldReturnLinksForAutoDiscoveryInBody() {
        val lastModified = Instant.ofEpochSecond(0)
        val ttl = 10L
        org.mockito.Mockito.`when`(osloBysykkelService!!.listStatuses())
            .thenReturn(Statuses(lastModified, ttl, listOf(
                StatusWithStation("42",
                    StationStatus.Data.Status(true, true, 10, 15, lastModified, true, "42"),
                    StationInformation.Data.Station("42", "Oslo S", "Oslo S", 1.0, 1.0, 15)
                )
            )))
        mockMvc!!.perform(get("/statuses/end-user-friendly"))
            .andExpect(status().isOk)
            .andExpect(header().string("Content-Type", "application/hal+json"))
            .andExpect(header().string("Cache-Control", "max-age=$ttl"))
            .andExpect(header().string("Last-Modified", StatusApiController.httpHeaderInstantFormatter.format(lastModified)))
            .andExpect(header().string("Expires", StatusApiController.httpHeaderInstantFormatter.format(lastModified.plusSeconds(ttl))))
            .andExpect(content().json(
                "{\n" +
                "  \"_embedded\": {\n" +
                "    \"statuses\": [\n" +
                "      {\n" +
                "        \"stationName\": \"Oslo S\",\n" +
                "        \"isRenting\": true,\n" +
                "        \"numBikesAvailable\": 10,\n" +
                "        \"isReturning\": true,\n" +
                "        \"numDocksAvailable\": 15,\n" +
                "        \"address\": \"Oslo S\",\n" +
                "        \"lastReported\": 0\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"_links\": {\n" +
                "    \"self\": {\n" +
                "      \"href\": \"http://localhost:8089/statuses/end-user-friendly\"\n" +
                "    }\n" +
                "  }\n" +
                "}"))
            .andDo(document("get-statuses-end-user-friendly"))
    }


}