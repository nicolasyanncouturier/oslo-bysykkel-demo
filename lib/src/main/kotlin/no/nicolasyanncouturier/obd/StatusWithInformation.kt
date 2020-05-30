package no.nicolasyanncouturier.obd

import no.nicolasyanncouturier.gbfs.StationInformation
import no.nicolasyanncouturier.gbfs.StationStatus

data class StatusWithInformation(val stationId: String,
                                 val status: StationStatus.Data.Status,
                                 val stationInformation: StationInformation.Data.Station)