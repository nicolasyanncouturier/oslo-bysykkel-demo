package no.nicolasyanncouturier.obd

import java.time.Instant

data class Statuses(val lastUpdated: Instant?, val ttl:Long?, val statusesWithStation:List<StatusWithStation>)