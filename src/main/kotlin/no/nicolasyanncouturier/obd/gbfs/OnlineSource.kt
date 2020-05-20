package no.nicolasyanncouturier.obd.gbfs

import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.Locale
import java.util.concurrent.TimeUnit

class OnlineSource(private val serviceDocumentUrl: String,
                   private val locale: Locale,
                   private val clientIdentifier: String?) : Source {

    private companion object {

        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)

        @JvmStatic
        private val httpClient = OkHttpClient.Builder().callTimeout(1, TimeUnit.SECONDS).build()

    }

    override fun fetchStationInformation(): StationInformation? {
        return try {
            getFeed("station_information")?.url?.let(this::getResource)
        } catch (e: IOException) {
            logger.warn("Failed to fetch station information", e)
            null
        }
    }

    override fun fetchStationStatus(): StationStatus? {
        return try {
            getFeed("station_status")?.url?.let(this::getResource)
        } catch (e: IOException) {
            logger.warn("Failed to fetch station status", e)
            null
        }
    }

    private fun getFeed(name: String): ServiceDocument.Feeds.Feed? {
        return fetchServiceDocument()?.data?.get(locale.language)?.feeds?.find { feed -> feed.name == name }
    }

    private fun fetchServiceDocument(): ServiceDocument? {
        return getResource(serviceDocumentUrl)
    }

    private inline fun <reified T : Any> getResource(url: String): T? {
        try {
            val request = Request.Builder().url(url);
            clientIdentifier?.run { request.addHeader("Client-Identifier", this) }
            request.addHeader("Accept", "application/json")
            httpClient.newCall(request.build()).execute().use { response ->
                if (response.isSuccessful) {
                    val contentType = response.header("Content-Type") ?: ""
                    if (contentType.split(";")[0].equals("application/json", ignoreCase = true)) {
                        val body = response.body()
                        if (body != null) {
                            try {
                                return Source.mapper.readValue(body.bytes(), T::class.java)
                            } catch (e: IOException) {
                                throw IOException("Could not parse body from response to call on $url", e)
                            }
                        } else {
                            throw IOException("No body in response to call on $url")
                        }
                    } else {
                        throw IOException("Unexpected Content-Type $contentType in response to call on $url")
                    }
                } else {
                    throw IOException("Could not get successful response to call on $url : HTTP " + response.code())
                }
            }
        } catch (e: SocketTimeoutException) {
            throw IOException("Call to $url timed out", e);
        } catch (e: IOException) {
            throw IOException("Call to $url failed", e);
        }
    }

}