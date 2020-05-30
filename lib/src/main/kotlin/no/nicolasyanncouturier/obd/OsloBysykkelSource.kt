package no.nicolasyanncouturier.obd

import no.nicolasyanncouturier.gbfs.Source
import no.nicolasyanncouturier.gbfs.StationInformation
import no.nicolasyanncouturier.gbfs.StationStatus
import org.springframework.stereotype.Component

@Component
class OsloBysykkelSource(private val source: Source) {

    private companion object {
        private var lastStationInformation: StationInformation? = null
        private var lastStationStatus: StationStatus? = null
    }

    fun fetchStationInformation(): StationInformation? {
        return source.fetchStationInformation()?.also { lastStationInformation = it } ?: lastStationInformation
    }

    fun fetchStationStatus(): StationStatus? {
        return source.fetchStationStatus()?.also { lastStationStatus = it } ?: lastStationStatus
    }

}