package no.nicolasyanncouturier.obd

import java.time.format.DateTimeFormatter

interface StatusViewDataProvider {

    companion object {

        @JvmStatic
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH.mm")

    }

    fun listStatuses(): List<StatusController.StatusViewData>

}