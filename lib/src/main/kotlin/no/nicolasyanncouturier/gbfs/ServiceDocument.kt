package no.nicolasyanncouturier.gbfs

import java.time.Instant

data class ServiceDocument(val lastUpdated: Instant?, val ttl: Int?, val data: Map<String, Feeds>?) {
    data class Feeds(val feeds: List<Feed>?) {
        data class Feed(val name: String?, val url: String?)
    }
}