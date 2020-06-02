package no.nicolasyanncouturier.lang

import kotlin.Pair
import spock.lang.Specification


class MapsSpec extends Specification {

    def "should left join maps"() {
        expect:
        Maps.leftJoin(left, right) == expected
        where:
        left                 | right                 | expected
        [:]                  | [:]                  || [:]
        ["id": 1]            | [:]                  || [:]
        [:]                  | ["id": 2]            || [:]
        ["id": 1]            | ["other": 2]         || [:]
        ["id": 1]            | ["id": 2]            || ["id": new Pair(1, 2)]
        ["id1": 1, "id2": 3] | ["id1": 2, "id3": 5] || ["id1": new Pair(1, 2)]
        ["id1": 1, "id2": 3] | ["id1": 2, "id2": 4] || ["id1": new Pair(1, 2), "id2": new Pair(3, 4)]
    }

}