package no.nicolasyanncouturier.obd

import org.springframework.hateoas.MediaTypes
import org.springframework.hateoas.client.Traverson
import org.springframework.hateoas.server.core.TypeReferences
import java.net.URI
import java.time.Instant
import java.time.LocalDateTime

class ApiStatusViewDataProvider : StatusViewDataProvider {

    override fun listStatuses(): List<StatusController.StatusViewData> {
        val collectionModelType: TypeReferences.CollectionModelType<Map<String,Any>> = MyType()
        val traverson = Traverson(URI.create("http://localhost:8089/"), MediaTypes.HAL_JSON)
        val collectionModel = traverson.follow("end-user-friendly-statuses").toObject(collectionModelType)
        return collectionModel?.content
            ?.mapNotNull { makeStatusViewData(it) }
            ?.toList()
            ?: emptyList()
    }

    class MyType: TypeReferences.CollectionModelType<Map<String,Any>>()

    private fun makeStatusViewData(props: Map<String, Any>): StatusController.StatusViewData? {
        return props["stationName"]?.toString()?.let { stationName ->
            StatusController.StatusViewData(
                stationName,
                props["isRenting"]?.toString()?.toBoolean(),
                props["numBikesAvailable"]?.toString()?.toInt(),
                props["isReturning"]?.toString()?.toBoolean(),
                props["numDocksAvailable"]?.toString()?.toInt(),
                props["address"].toString(),
                props["lastReported"]?.toString()?.toLong()?.let { lastReported ->
                    StatusViewDataProvider.dateTimeFormatter.format(
                        LocalDateTime.ofInstant(Instant.ofEpochSecond(lastReported), DefaultRegion.zone))
                })
        }
    }

}