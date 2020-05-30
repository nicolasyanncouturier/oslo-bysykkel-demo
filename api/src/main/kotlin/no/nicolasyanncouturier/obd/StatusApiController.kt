package no.nicolasyanncouturier.obd

import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StatusApiController(private val service: OsloBysykkelService) {

    @GetMapping("/statuses/end-user-friendly", produces = [ "application/hal+json" ])
    fun listEndUserFriendlyStatuses(): CollectionModel<StatusApiData> {
        val statuses = service.listStatuses().mapNotNull { StatusApiData.makeStatusApiData(it) }.toList()
        val self = linkTo(methodOn(StatusApiController::class.java).listEndUserFriendlyStatuses()).withSelfRel()
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
            fun makeStatusApiData(statusWithInformation: StatusWithInformation): StatusApiData? {
                return statusWithInformation.stationInformation.name?.let { name ->
                    StatusApiData(name,
                        statusWithInformation.status.isRenting,
                        statusWithInformation.status.numBikesAvailable,
                        statusWithInformation.status.isReturning,
                        statusWithInformation.status.numDocksAvailable,
                        statusWithInformation.stationInformation.address,
                        statusWithInformation.status.lastReported?.toEpochMilli())
                }
            }
        }
    }

}