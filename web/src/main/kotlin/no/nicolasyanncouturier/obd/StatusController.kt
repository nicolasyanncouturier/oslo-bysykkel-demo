package no.nicolasyanncouturier.obd

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Controller
class StatusController(private val statusViewDataProvider: StatusViewDataProvider) {



    @GetMapping("/", produces = ["text/html"])
    fun listStatuses(model: Model): String {
        model["title"] = "Tilgjengelihet på stasjon"
        model["statuses"] = statusViewDataProvider.listStatuses()
        return "statuses"
    }

    data class StatusViewData(val stationName: String,
                              val isRenting: Boolean?,
                              val numBikesAvailable: Int?,
                              val isReturning: Boolean?,
                              val numDocksAvailable: Int?,
                              val address: String?,
                              val lastReported: String?)

}