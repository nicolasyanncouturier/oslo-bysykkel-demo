package no.nicolasyanncouturier.obd

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.servlet.http.HttpServletResponse

@RestController
class StatusApiController(private val service: OsloBysykkelService) {

    companion object {

        @JvmStatic
        private val httpHeaderInstantFormatter = DateTimeFormatter.RFC_1123_DATE_TIME
            .withLocale(Locale.UK)
            .withZone(ZoneId.of("GMT"))

    }

    @Autowired
    private lateinit var response: HttpServletResponse

    @GetMapping("/statuses/end-user-friendly", produces = ["application/hal+json"])
    fun listEndUserFriendlyStatuses(): CollectionModel<StatusApiData> {
        val statusesWithMeta = service.listStatuses()
        val statuses = statusesWithMeta.statusesWithStation.mapNotNull { StatusApiData.makeStatusApiData(it) }.toList()
        val self = linkTo(methodOn(StatusApiController::class.java).listEndUserFriendlyStatuses()).withSelfRel()
        statusesWithMeta.lastUpdated?.let { lu ->
            response.addHeader("Last-Modified", httpHeaderInstantFormatter.format(lu))
            statusesWithMeta.ttl?.run {
                response.addHeader("Expires", httpHeaderInstantFormatter.format(lu.plusSeconds(this)))
            }
        }
        statusesWithMeta.ttl?.run {
            response.addHeader("Cache-Control", "max-age=" + this)
        }
        return CollectionModel.of(statuses, self)
    }

    @Relation(collectionRelation = "statuses", itemRelation = "status")
    data class StatusApiData(val stationName: String,
                             val isRenting: Boolean?,
                             val numBikesAvailable: Int?,
                             val isReturning: Boolean?,
                             val numDocksAvailable: Int?,
                             val address: String?,
                             val lastReported: Long?) : RepresentationModel<StatusApiData>() {
        companion object {
            fun makeStatusApiData(statusWithStation: StatusWithStation): StatusApiData? {
                return statusWithStation.stationInformation.name?.let { name ->
                    StatusApiData(name,
                        statusWithStation.status.isRenting,
                        statusWithStation.status.numBikesAvailable,
                        statusWithStation.status.isReturning,
                        statusWithStation.status.numDocksAvailable,
                        statusWithStation.stationInformation.address,
                        statusWithStation.status.lastReported?.epochSecond)
                }
            }
        }
    }



}