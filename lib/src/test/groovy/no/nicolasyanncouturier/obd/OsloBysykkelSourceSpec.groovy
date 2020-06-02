package no.nicolasyanncouturier.obd

import no.nicolasyanncouturier.gbfs.Source
import no.nicolasyanncouturier.gbfs.StationInformation
import no.nicolasyanncouturier.gbfs.StationStatus
import spock.lang.Specification

import java.time.Instant

class OsloBysykkelSourceSpec extends Specification {

    def "Source should return last fetched station information when subsequent call returns nothing"() {
        given:
        def source = Mock(Source)
        def obdSource = new OsloBysykkelSource(source)
        def stationInformation = new StationInformation(Instant.now(), 10, new StationInformation.Data([]))

        when:
        1 * source.fetchStationInformation() >> stationInformation

        then:
        obdSource.fetchStationInformation() == stationInformation

        when:
        1 * source.fetchStationInformation() >> null

        then:
        obdSource.fetchStationInformation() == stationInformation
    }

    def "Source should return last fetched station status when subsequent call returns nothing"() {
        given:
        def source = Mock(Source)
        def obdSource = new OsloBysykkelSource(source)
        def stationStatus = new StationStatus(Instant.now(), 10, new StationStatus.Data([]))

        when:
        1 * source.fetchStationStatus() >> stationStatus

        then:
        obdSource.fetchStationStatus() == stationStatus

        when:
        1 * source.fetchStationStatus() >> null

        then:
        obdSource.fetchStationStatus() == stationStatus
    }

}