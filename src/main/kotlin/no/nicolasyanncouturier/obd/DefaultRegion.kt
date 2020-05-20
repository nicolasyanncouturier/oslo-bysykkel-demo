package no.nicolasyanncouturier.obd

import java.time.ZoneId
import java.util.Locale

object DefaultRegion {

    val locale: Locale = Locale.Builder().setLanguage("nb").setRegion("NO").build()

    val zone: ZoneId = ZoneId.of("Europe/Oslo")

}