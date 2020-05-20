package no.nicolasyanncouturier.obd.gbfs

import com.fasterxml.jackson.module.kotlin.readValue
import java.io.InputStream

class OfflineSource : Source {

    override fun fetchStationInformation(): StationInformation? {
        return getResourceAsStream("/local_gbfs/station_information.json")
            ?.let { Source.mapper.readValue(it) }
    }

    override fun fetchStationStatus(): StationStatus? {
        return getResourceAsStream("/local_gbfs/station_status.json")
            ?.let { Source.mapper.readValue(it) }
    }

    private fun getResourceAsStream(resource: String): InputStream? =
        Thread.currentThread().contextClassLoader.getResourceAsStream(resource)
            ?: resource::class.java.getResourceAsStream(resource)

}