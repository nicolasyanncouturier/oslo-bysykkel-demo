package no.nicolasyanncouturier.obd

import no.nicolasyanncouturier.lang.Maps
import no.nicolasyanncouturier.obd.gbfs.StationInformation
import no.nicolasyanncouturier.obd.gbfs.StationStatus
import org.springframework.stereotype.Service

@Service
class OsloBysykkelService(private val osloBysykkelSource: OsloBysykkelSource) {

    companion object {
        @JvmStatic
        private val hasName = { station: StationInformation.Data.Station -> station.name != null && !station.name.isBlank() }

        @JvmStatic
        private val isOfInterestToUser = { status: StationStatus.Data.Status ->
            status.existsPhysically() && (status.isRentingAndHasBikes() || status.isReturningAndHasDocks())
        }
    }

    fun listStatuses(): List<StatusWithInformation> {
        val stationStatusById = getStationStatusById()
        val stationInformationById = getStationInformationById()
        return Maps.leftJoin(stationStatusById, stationInformationById)
            .map { entry -> StatusWithInformation(entry.key, entry.value.first, entry.value.second) }
            .sortedBy { statusWithInformation -> statusWithInformation.stationInformation.name }
            .toList()
    }

    private fun getStationInformationById(): Map<String, StationInformation.Data.Station> {
        return osloBysykkelSource.fetchStationInformation()?.data?.stations
            ?.filter(hasName)
            ?.mapNotNull { station -> station.id?.let { id -> id to station } }
            ?.toMap()
            ?: emptyMap()
    }

    private fun getStationStatusById(): Map<String, StationStatus.Data.Status> {
        return osloBysykkelSource.fetchStationStatus()?.data?.statuses
            ?.filter(isOfInterestToUser)
            ?.mapNotNull { status -> status.stationId?.let { id -> id to status } }
            ?.toMap()
            ?: emptyMap()
    }

}