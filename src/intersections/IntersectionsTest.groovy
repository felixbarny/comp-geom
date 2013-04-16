package intersections

@Grab(group = 'org.spockframework', module = 'spock-core', version = '0.7-groovy-2.0') @GrabExclude('org.codehaus.groovy:groovy-all')
import spock.lang.*

class IntersectionsTest extends Specification {
    def "stretch intersection test"() {
        expect:
        Stretch.valueOf(stretch1).intersects(Stretch.valueOf(stretch2)) == intersects

        where:
        stretch1       | stretch2                   | intersects
        [0, 0, 0, 10]  | [0, 9, 0, 20]              | true  // on one line
        [0, 0, 0, 20]  | [0, 5, 0, 10]              | true
        [0, 0, 0, 10]  | [0, 5, 0, 20]              | true

        [0, 0, 0, 10]  | [0, 0, 0, 10]              | true  // same stretches
        [0, 0, 0, 0]   | [0, 0, 0, 0]               | true

        [0, 0, 0, 10]  | [0, 10, 0, 20]             | true  // touching in one point
        [0, 0, 0, 10]  | [0, 10, 0, 10]             | true
        [0, 0, 10, 10] | [10, 10, 20, 20]           | true
        [0, 0, 10, 10] | [0, 5, 5, 5]               | true
        [0, 0, 0, 10]  | [0, 0, 10, 0]              | true

        [0, 0, 0, 10]  | [0.0001, 5, 5, 5]          | false // almost touching
        [0, 0, 0, 0]   | [0.0001, 0, 0.0001, 0]     | false // two points
        [0, 0, 0, 10]  | [1, 5, 5, 5]               | false
        [0, 0, 10, 10] | [10.0001, 10.0001, 20, 20] | false
        [0, 0, 10, 10] | [11, 11, 20, 20]           | false
        [0, 0, 0, 10]  | [0, 11, 0, 20]             | false

        [0, 0, 0, 10]  | [0, 10, 0, 10]             | true  // one point
        [0, 0, 0, 10]  | [0, 11, 0, 11]             | false
        [0, 0, 0, 10]  | [5, 5, 5, 5]               | false

        [0, 0, 0, 0]   | [123, 645, 123, 645]       | false // two points
        [0, 0, 0, 0]   | [0, 0, 0, 0]               | true

        [0, 0, 0, 10]  | [0, 20, 0, 30]             | false // same x, different y

        [0, 0, 0, 10]  | [1, 1, 1, 10]              | false // parallel
        [0, 0, 0, 10]  | [0, 0.1, 1, 10]            | false // almost parallel
    }
}