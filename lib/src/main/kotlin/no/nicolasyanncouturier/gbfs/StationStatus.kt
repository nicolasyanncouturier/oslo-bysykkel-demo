package no.nicolasyanncouturier.gbfs

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class StationStatus(val lastUpdated: Instant?, val ttl: Int?, val data: Data?) {
    data class Data(@JsonProperty("stations") val statuses: List<Status>?) {
        data class Status(
            val isInstalled: Boolean?,
            val isRenting: Boolean?,
            val numBikesAvailable: Int?,
            val numDocksAvailable: Int?,
            val lastReported: Instant?,
            val isReturning: Boolean?,
            val stationId: String?) {

            fun existsPhysically(): Boolean {
                return isInstalled ?: false;
            }

            fun isRentingAndHasBikes(): Boolean {
                return (isRenting ?: false) && numBikesAvailable != null && numBikesAvailable >= 0
            }

            fun isReturningAndHasDocks(): Boolean {
                return (isReturning ?: false) && numDocksAvailable != null && numDocksAvailable >= 0
            }
        }
    }
}