package intersections

@Grab(group = 'org.spockframework', module = 'spock-core', version = '0.7-groovy-2.0') @GrabExclude('org.codehaus.groovy:groovy-all')
import spock.lang.*

class IntersectionsTest extends Specification {
    def "simple stretches are calculated correctly"() {
        expect:
        Stretch.valueOf(stretch1).intersects(Stretch.valueOf(stretch2)) == intersects

        where:
        stretch1       | stretch2            | intersects
        [0, 0, 10, 10] | [0, 5, 5, 5]        | true  // touching in one point
        [0, 0, 0, 10]  | [0, 0, 10, 0]       | true
        [0, 0, 10, 0]  | [5, 0, 10, 20]      | true
        [0, 0, 10, 0]  | [10, 0, 10, 20]     | true

        [0, 0, 0, 10]  | [0.0001, 5, 5, 5]   | false // almost touching
        [0, 0, 0, 10]  | [1, 0, 1, 10]       | false // parallel
        [0, 0, 10, 0]  | [0, 0.00001, 1, 10] | false // almost parallel
        [0, 0, 0, 10]  | [-1, 11, 11, 11]    | false
        [0, 0, 0, 10]  | [0, 11, 11, 11]     | false
    }

    def "kolinear stretches are calculated correctly"() {
        expect:
        Stretch.valueOf(stretch1).intersects(Stretch.valueOf(stretch2)) == intersects

        where:
        stretch1       | stretch2                   | intersects
        [0, 0, 10, 10] | [10.0001, 10.0001, 20, 20] | false
        [0, 0, 0, 10]  | [0, 11, 0, 20]             | false
        [0, 0, 0, 10]  | [0, 20, 0, 30]             | false
        [0, 0, 0, 10]  | [0, 9, 0, 20]              | true
        [0, 0, 0, 20]  | [0, 5, 0, 10]              | true
        [0, 0, 0, 10]  | [0, 5, 0, 20]              | true
        [0, 0, 10, 0]  | [5, 0, 20, 0]              | true  // right
        [5, 0, 20, 0]  | [0, 0, 10, 0]              | true  // left
        [0, 0, 0, 10]  | [0, 0, 0, 10]              | true  // same stretches

        [0, 0, 0, 10]  | [0, 10, 0, 20]             | true  // touching in one point
        [0, 0, 10, 10] | [10, 10, 20, 20]           | true
        [0, 0, 0, 10]  | [0, 10, 0, 10]             | true
    }

    def "stretches that are points are calculated correctly"() {
        expect:
        Stretch.valueOf(stretch1).intersects(Stretch.valueOf(stretch2)) == intersects

        where:
        stretch1         | stretch2                  | intersects
        [0, 0, 0, 10]    | [0, 10, 0, 10]            | true  // one point
        [0, 0, 0, 10]    | [0, 11, 0, 11]            | false
        [0, 0, 0, 10]    | [5, 5, 5, 5]              | false // right one is point
        [5, 5, 5, 5]     | [0, 0, 0, 10]             | false // left one is point

        [0, 0, 0, 0]     | [0.0001, 0, 0.0001, 0]    | false // two points
        [0, 0, 10, 10]   | [11, 11, 20, 20]          | false
        [0, 0, 0, 0]     | [123, 645, 123, 645]      | false
        [0, 0, 0, 0]     | [0, 0, 0, 0]              | true  // same points
        [0, 0, 0, 0]     | [-1, -1, -1, -1]          | false  // left
        [0, 0, 0, 0]     | [1, 1, 1, 1]              | false  // right


    }

    def "parallel left stretch is calculated correctly"() {
        expect:
        Stretch.valueOf(stretch).getLeftParallelStretch() == Stretch.valueOf(parallel)

        where:
        stretch        | parallel
        [0, 1, 2, 0]   | [1, 3, 3, 2]
        [1, 2, 4.5, 5] | [-2, 5.5, 1.5, 8.5]
        [-2, 1, 2, 0]  | [-1, 5, 3, 4]
    }
}