package no.nicolasyanncouturier.obd

import no.nicolasyanncouturier.obd.gbfs.StationInformation
import no.nicolasyanncouturier.obd.gbfs.StationStatus

data class StatusWithInformation(val stationId: String,
                                 val status: StationStatus.Data.Status,
                                 val stationInformation: StationInformation.Data.Station)