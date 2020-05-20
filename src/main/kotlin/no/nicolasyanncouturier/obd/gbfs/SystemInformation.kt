package no.nicolasyanncouturier.obd.gbfs

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class SystemInformation(val lastUpdated: Instant, val ttl: Long, @JsonProperty("data") val system: System) {
    data class System(@JsonProperty("system_id") val id: String,
                      val language: String,
                      val name: String,
                      val operator: String,
                      val timezone: String,
                      val phone_number: String,
                      val email: String)
}