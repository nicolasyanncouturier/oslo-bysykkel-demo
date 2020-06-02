package no.nicolasyanncouturier.obd

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Controller
class StatusController(private val service: OsloBysykkelService) {

    companion object {

        @JvmStatic
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH.mm")

    }

    @GetMapping("/", produces = ["text/html"])
    fun listStatuses(model: Model): String {
        model["title"] = "Tilgjengelihet på stasjon"
        val viewData: List<StatusViewData> = service.listStatuses().statusesWithStation
            .mapNotNull {StatusViewData.makeStatusViewData(it)}
            .toList()
        model["statuses"] = viewData
        return "statuses"
    }

    data class StatusViewData(val stationName: String,
                              val isRenting: Boolean?,
                              val numBikesAvailable: Int?,
                              val isReturning: Boolean?,
                              val numDocksAvailable: Int?,
                              val address: String?,
                              val lastReported: String?) {
        companion object {
            fun makeStatusViewData(statusWithStation: StatusWithStation): StatusViewData? {
                return statusWithStation.stationInformation.name?.let { name ->
                    StatusViewData(name,
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
    }

}