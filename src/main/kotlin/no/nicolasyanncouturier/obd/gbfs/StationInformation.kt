package no.nicolasyanncouturier.obd.gbfs

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class StationInformation(val lastUpdated: Instant?, val data: Data?) {
    data class Data(val stations: List<Station>?) {
        data class Station(@JsonProperty("station_id") val id: String?,
                           val name: String?,
                           val address: String?,
                           val lat: Double?,
                           val lon: Double?,
                           val capacity: Int?)
    }
}