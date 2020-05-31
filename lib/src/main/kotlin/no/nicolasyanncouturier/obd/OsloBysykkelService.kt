package no.nicolasyanncouturier.obd

import no.nicolasyanncouturier.gbfs.StationInformation
import no.nicolasyanncouturier.gbfs.StationStatus
import no.nicolasyanncouturier.lang.Maps
import org.springframework.stereotype.Service
import java.time.Instant

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

    fun listStatuses(): List<StatusWithStation> {
        return listStatusesWithMetaInfo().statusesWithStation;
    }

    fun listStatusesWithMetaInfo(): Statuses {
        val status = osloBysykkelSource.fetchStationStatus()
        val information = osloBysykkelSource.fetchStationInformation()
        val statusesWithStation = Maps.leftJoin(
            indexStationStatusById(status?.data?.statuses),
            indexStationInformationById(information?.data?.stations))
            .map { entry -> StatusWithStation(entry.key, entry.value.first, entry.value.second) }
            .sortedBy { statusWithInformation -> statusWithInformation.stationInformation.name }
            .toList()
        return Statuses(status?.lastUpdated, status?.ttl, statusesWithStation)
    }

    private fun indexStationInformationById(stations: List<StationInformation.Data.Station>?): Map<String, StationInformation.Data.Station> {
        return stations
            ?.filter(hasName)
            ?.mapNotNull { station -> station.id?.let { id -> id to station } }
            ?.toMap()
            ?: emptyMap()
    }

    private fun indexStationStatusById(statuses: List<StationStatus.Data.Status>?): Map<String, StationStatus.Data.Status> {
        return statuses
            ?.filter(isOfInterestToUser)
            ?.mapNotNull { status -> status.stationId?.let { id -> id to status } }
            ?.toMap()
            ?: emptyMap()
    }

    private fun mostRecent(instant1: Instant?, instant2: Instant?): Instant? {
        return if (instant1 != null && instant2 != null) {
            if (instant1 > instant2) instant1 else instant2
        } else instant1 ?: instant2
    }

}