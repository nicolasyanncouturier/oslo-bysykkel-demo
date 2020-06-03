package no.nicolasyanncouturier.obd

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*


@RunWith(SpringRunner::class)
@WebMvcTest(RootApiController::class)
@AutoConfigureRestDocs(outputDir = "target/snippets", uriPort = 8089)
class RootApiControllerTest {
    @Autowired
    private val mockMvc: MockMvc? = null

    @Test
    @Throws(Exception::class)
    fun shouldReturnLinksForAutoDiscovery() {
        mockMvc!!.perform(head("/"))
            .andExpect(status().isNoContent)
            .andExpect(header().stringValues("Link", "<http://localhost:8089/>;rel=\"self\"", "<http://localhost:8089/statuses/end-user-friendly>;rel=\"end-user-friendly-statuses\""))
            .andDo(document("head-root"))
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturnLinksForAutoDiscoveryInBody() {
        mockMvc!!.perform(get("/"))
            .andExpect(status().isOk)
            .andExpect(header().string("Content-Type", "application/hal+json"))
            .andExpect(content().json("{\"_links\":{\"self\":{\"href\":\"http://localhost:8089/\"},\"end-user-friendly-statuses\":{\"href\":\"http://localhost:8089/statuses/end-user-friendly\"}}}"))
            .andDo(document("get-root"))
    }


}