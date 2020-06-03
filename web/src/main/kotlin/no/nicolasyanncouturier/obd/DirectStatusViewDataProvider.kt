package no.nicolasyanncouturier.obd

import no.nicolasyanncouturier.obd.StatusViewDataProvider.Companion.dateTimeFormatter
import java.time.LocalDateTime

class DirectStatusViewDataProvider(private val osloBysykkelService: OsloBysykkelService): StatusViewDataProvider {

    override fun listStatuses(): List<StatusController.StatusViewData> {
        return osloBysykkelService.listStatuses().statusesWithStation
            .mapNotNull { makeStatusViewData(it)}
            .toList()
    }

    private fun makeStatusViewData(statusWithStation: StatusWithStation): StatusController.StatusViewData? {
        return statusWithStation.stationInformation.name?.let { name ->
            StatusController.StatusViewData(name,
                statusWithStation.status.isRenting,
                statusWithStation.status.numBikesAvailable,
                statusWithStation.status.isReturning,
                statusWithStation.status.numDocksAvailable,
                statusWithStation.stationInformation.address,
                statusWithStation.status.lastReported?.let { lastReported ->
                    dateTimeFormatter.format(
                        LocalDateTime.ofInstant(lastReported, DefaultRegion.zone))
                }
            )
        }
    }

}