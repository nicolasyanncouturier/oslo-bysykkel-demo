package no.nicolasyanncouturier.obd

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@RestController
class RootApiController {

    @Autowired
    private lateinit var response: HttpServletResponse

    @RequestMapping(method = [RequestMethod.HEAD], path = ["/"], produces = ["text/plain"])
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun listResourcesMini() {
        addLinks(getLinks());
    }

    @GetMapping("/", produces = ["application/hal+json"])
    fun listResources(): CollectionModel<String> {
        val links = getLinks()
        addLinks(links)
        return CollectionModel.of(emptySet(), links)
    }

    private fun addLinks(links: Set<Link>) {
        links.forEach { link ->
            response.addHeader("Link", link.toString())
        }
    }

    private fun getLinks(): Set<Link> {
        return setOf(
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RootApiController::class.java)
                .listResources()).withSelfRel(),
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StatusApiController::class.java)
                .listEndUserFriendlyStatuses()).withRel("end-user-friendly-statuses")
        )
    }

}