package no.nicolasyanncouturier.obd

import no.nicolasyanncouturier.gbfs.StationInformation
import no.nicolasyanncouturier.gbfs.StationStatus
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant

class OsloBysykkelServiceSpec extends Specification {

    static def ID = "id"

    static def NOW = Instant.now()

    static def TTL = 10

    static def A_STATION = new StationInformation.Data.Station(ID, "name", "address", 1.0, 1.0, 10)

    static def A_STATION_INFORMATION = new StationInformation(NOW, TTL, new StationInformation.Data([A_STATION]))

    static def A_STATUS = new StationStatus.Data.Status(true, true, 1, 1, NOW, true, ID)

    static def A_STATION_STATUS = new StationStatus(NOW, TTL, new StationStatus.Data([A_STATUS]))

    static def EMPTY_STATUSES = new Statuses(NOW, TTL, [])

    @Unroll
    def "Listing statuses tolerates the source returning nothing"() {
        given:
        def source = Mock(OsloBysykkelSource)
        def service = new OsloBysykkelService(source)

        when:
        source.fetchStationInformation() >> station
        source.fetchStationStatus() >> status

        then:
        service.listStatuses() == expected

        where:
        station                                                             | status                                                    || expected
        A_STATION_INFORMATION                                               | null                                                      || new Statuses(null, null, [])
        A_STATION_INFORMATION                                               | new StationStatus(NOW, TTL, null)                         || EMPTY_STATUSES
        A_STATION_INFORMATION                                               | new StationStatus(NOW, TTL, new StationStatus.Data(null)) || EMPTY_STATUSES
        A_STATION_INFORMATION                                               | new StationStatus(NOW, TTL, new StationStatus.Data([]))   || EMPTY_STATUSES
        null                                                                | A_STATION_STATUS                                          || EMPTY_STATUSES
        new StationInformation(NOW, TTL, null)                              | A_STATION_STATUS                                          || EMPTY_STATUSES
        new StationInformation(NOW, TTL, new StationInformation.Data(null)) | A_STATION_STATUS                                          || EMPTY_STATUSES
        new StationInformation(NOW, TTL, new StationInformation.Data([]))   | A_STATION_STATUS                                          || EMPTY_STATUSES
        null                                                                | null                                                      || new Statuses(null, null, [])
    }

    @Unroll
    def "Listing statuses skips stations without name"() {
        given:
        def source = Mock(OsloBysykkelSource)
        def service = new OsloBysykkelService(source)

        when:
        source.fetchStationInformation() >> new StationInformation(NOW, TTL, new StationInformation.Data([
            station
        ]))
        source.fetchStationStatus() >> A_STATION_STATUS

        then:
        service.listStatuses() == EMPTY_STATUSES

        where:
        station << [
            new StationInformation.Data.Station(ID, null, "address", 1.0, 1.0, 10),
            new StationInformation.Data.Station(ID, "", "address", 1.0, 1.0, 10),
            new StationInformation.Data.Station(ID, "  ", "address", 1.0, 1.0, 10),
            new StationInformation.Data.Station(ID, "\t", "address", 1.0, 1.0, 10)
        ]
    }

    @Unroll
    def "Listing statuses skips stations that are not installed"() {
        given:
        def source = Mock(OsloBysykkelSource)
        def service = new OsloBysykkelService(source)

        when:
        source.fetchStationInformation() >> A_STATION_INFORMATION
        source.fetchStationStatus() >> new StationStatus(NOW, TTL, new StationStatus.Data([
            status
        ]))

        then:
        service.listStatuses() == EMPTY_STATUSES

        where:
        status << [
            new StationStatus.Data.Status(null, true, 1, 1, NOW, true, ID),
            new StationStatus.Data.Status(false, true, 1, 1, NOW, true, ID)
        ]
    }

    @Unroll
    def "Listing statuses skips stations that are neither returning nor renting"() {
        given:
        def source = Mock(OsloBysykkelSource)
        def service = new OsloBysykkelService(source)

        when:
        source.fetchStationInformation() >> A_STATION_INFORMATION
        source.fetchStationStatus() >> new StationStatus(NOW, TTL, new StationStatus.Data([
            status
        ]))

        then:
        service.listStatuses() == EMPTY_STATUSES

        where:
        status << [
            new StationStatus.Data.Status(true, null, 1, 1, NOW, null, ID),
            new StationStatus.Data.Status(true, null, 1, 1, NOW, false, ID),
            new StationStatus.Data.Status(true, false, 1, 1, NOW, null, ID),
            new StationStatus.Data.Status(true, false, 1, 1, NOW, false, ID)
        ]
    }

    @Unroll
    def "Listing statuses skips stations that lacking number of available bikes or locks"() {
        given:
        def source = Mock(OsloBysykkelSource)
        def service = new OsloBysykkelService(source)

        when:
        source.fetchStationInformation() >> A_STATION_INFORMATION
        source.fetchStationStatus() >> new StationStatus(NOW, TTL, new StationStatus.Data([
            status
        ]))

        then:
        service.listStatuses() == EMPTY_STATUSES

        where:
        status << [
            new StationStatus.Data.Status(true, true, null, 1, NOW, false, ID),
            new StationStatus.Data.Status(true, true, -1, 1, NOW, false, ID),
            new StationStatus.Data.Status(true, false, 1, null, NOW, true, ID),
            new StationStatus.Data.Status(true, false, 1, -1, NOW, true, ID)
        ]
    }

    @Unroll
    def "Listing statuses keeps stations that are at least renting or at least returning"() {
        given:
        def source = Mock(OsloBysykkelSource)
        def service = new OsloBysykkelService(source)
        def station = A_STATION

        when:
        source.fetchStationInformation() >> new StationInformation(NOW, TTL, new StationInformation.Data([
            station
        ]))
        source.fetchStationStatus() >> new StationStatus(NOW, TTL, new StationStatus.Data([
            status
        ]))

        then:
        service.listStatuses() == new Statuses(NOW, TTL, [new StatusWithStation(ID, status, station)])

        where:
        status << [
            new StationStatus.Data.Status(true, true, 1, 1, NOW, false, ID),
            new StationStatus.Data.Status(true, false, 1, 1, NOW, true, ID)
        ]
    }

    def "Listing statuses keeps the last updated date and TTL of the status information, not the station information"() {
        given:
        def source = Mock(OsloBysykkelSource)
        def service = new OsloBysykkelService(source)
        def lastUpdated = NOW.minusSeconds(30)
        def ttl = 5
        when:
        source.fetchStationInformation() >> new StationInformation(NOW, TTL, new StationInformation.Data([
            A_STATION
        ]))
        source.fetchStationStatus() >> new StationStatus(lastUpdated, ttl, new StationStatus.Data([
            A_STATUS
        ]))

        then:
        service.listStatuses() == new Statuses(lastUpdated, ttl, [new StatusWithStation(ID, A_STATUS, A_STATION)])
    }

}