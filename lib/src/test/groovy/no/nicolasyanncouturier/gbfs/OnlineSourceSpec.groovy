package no.nicolasyanncouturier.gbfs

import okhttp3.*
import okio.BufferedSource
import spock.lang.Specification
import spock.lang.Unroll

class OnlineSourceSpec extends Specification {

    static def SERVICE_DOCUMENT_URL = "http://example.com"

    static def CLIENT_IDENTIFIER = "myclient"

    def "fetching service document fails when http call fails"() {
        given:
        def httpClient = Mock(OkHttpClient)
        httpClient.newCall(_) >> Mock(Call) {
            execute() >> aHttpFailure(500)
        }
        def source = new OnlineSource(SERVICE_DOCUMENT_URL, Locale.ENGLISH, CLIENT_IDENTIFIER, httpClient)

        when:
        source.fetchServiceDocument()

        then:
        thrown IOException
    }

    def "fetching service document returns a service document"() {
        given:
        def httpClient = Mock(OkHttpClient)
        1 * httpClient.newCall(_) >> Mock(Call) {
            execute() >> aHttpSuccess('{"data":{"en":{"feeds":[{"name":"station_information","url":"http://example.com"}]}}}')
        }
        def source = new OnlineSource(SERVICE_DOCUMENT_URL, Locale.ENGLISH, CLIENT_IDENTIFIER, httpClient)

        when:
        def serviceDocument = source.fetchServiceDocument()

        then:
        serviceDocument.data["en"].feeds == [new ServiceDocument.Feeds.Feed("station_information", "http://example.com")]
    }

    def "fetching station information returns nothing when fetching service document fails" () {
        given:
        def httpClient = Mock(OkHttpClient)
        1 * httpClient.newCall(_) >> Mock(Call) {
            execute() >> { throw new IOException("some failure") }
        }
        def source = new OnlineSource(SERVICE_DOCUMENT_URL, Locale.ENGLISH, CLIENT_IDENTIFIER, httpClient)

        when:
        def info = source.fetchStationInformation()

        then:
        info == null
    }

    @Unroll
    def "fetching station information returns nothing when fetching service document returns an HTTP error" () {
        given:
        def httpClient = Mock(OkHttpClient)
        1 * httpClient.newCall(_) >> Mock(Call) {
            execute() >> aHttpFailure(code)
        }
        def source = new OnlineSource(SERVICE_DOCUMENT_URL, Locale.ENGLISH, CLIENT_IDENTIFIER, httpClient)

        when:
        def info = source.fetchStationInformation()

        then:
        info == null

        where:
        code << [400, 401, 403, 404, 500]
    }

    @Unroll
    def "fetching station information returns nothing when fetching service document with malformed content" () {
        given:
        def httpClient = Mock(OkHttpClient)
        1 * httpClient.newCall(_) >> Mock(Call) {
            execute() >> aHttpSuccess(body)
        }
        def source = new OnlineSource(SERVICE_DOCUMENT_URL, Locale.ENGLISH, CLIENT_IDENTIFIER, httpClient)

        when:
        def info = source.fetchStationInformation()

        then:
        info == null

        where:
        body << ["", "{}", "[]", "dwertg"]
    }

    @Unroll
    def "fetching station information returns nothing when fetched service document contains no matching feed" () {
        given:
        def httpClient = Mock(OkHttpClient)
        1 * httpClient.newCall(_) >> Mock(Call) {
            execute() >> aHttpSuccess('{"data":{"en":{"feeds":[{"name":"station_status","url":"http://example.com"}]}}}')
        }
        def source = new OnlineSource(SERVICE_DOCUMENT_URL, Locale.ENGLISH, CLIENT_IDENTIFIER, httpClient)

        when:
        def info = source.fetchStationInformation()

        then:
        info == null
    }

    @Unroll
    def "fetching station information returns nothing when fetched station content is malformed" () {
        given:
        def httpClient = Mock(OkHttpClient)
        1 * httpClient.newCall(_) >> Mock(Call) {
            execute() >> aHttpSuccess('{"data":{"en":{"feeds":[{"name":"station_information","url":"http://example.com"}]}}}')
        }
        1 * httpClient.newCall(_) >> Mock(Call) {
            execute() >> aHttpSuccess(body)
        }
        def source = new OnlineSource(SERVICE_DOCUMENT_URL, Locale.ENGLISH, CLIENT_IDENTIFIER, httpClient)

        when:
        def info = source.fetchStationInformation()

        then:
        info == null

        where:
        body << ["", "[]", "dwertg"]
    }

    def "fetching station information returns station information" () {
        given:
        def httpClient = Mock(OkHttpClient)
        1 * httpClient.newCall(_) >> Mock(Call) {
            execute() >> aHttpSuccess('{"data":{"en":{"feeds":[{"name":"station_information","url":"http://example.com"}]}}}')
        }
        1 * httpClient.newCall(_) >> Mock(Call) {
            execute() >> aHttpSuccess('{"data":{"stations":[{"station_id":"42","name":"station"}]}}')
        }
        def source = new OnlineSource(SERVICE_DOCUMENT_URL, Locale.ENGLISH, CLIENT_IDENTIFIER, httpClient)

        when:
        def info = source.fetchStationInformation()

        then:
        info.data.stations == [new StationInformation.Data.Station("42", "station", null, null, null, null)]
    }

    def aHttpFailure(int code) {
        new Response.Builder()
            .request(new Request.Builder()
                .url("http://example.cpm").build())
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .body(Mock(ResponseBody))
            .message("")
            .build()
    }

    def aHttpSuccess(String body) {
        new Response.Builder()
            .request(new Request.Builder()
                .url("http://example.cpm").build())
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .header("Content-Type", "application/json")
            .body(Mock(ResponseBody) {
                contentLength() >> body.size()
                source() >> Mock(BufferedSource) {
                    readByteArray() >> body.getBytes("UTF-8")
                }
            })
            .message("")
            .build()
    }

}