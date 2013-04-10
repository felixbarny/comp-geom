package intersections

import groovy.transform.Immutable;

def stretches = []
new File('Strecken_1000.dat').text.eachLine { String line, lineNumber ->
    def points = line.split(" ")
    stretches << new Stretch(lineNumber: lineNumber + 1,
            a: new Coordinate(x: points[0] as BigDecimal, y: points[1] as BigDecimal),
            b: new Coordinate(x: points[2] as BigDecimal, y: points[3] as BigDecimal))
}

stretches.each { stretch1 ->
    stretches.each { stretch2 ->
        if (!stretch1.is(stretch2)) println "${stretch1.lineNumber} - ${stretch2.lineNumber}: ${stretch1.intersects(stretch2)}"
    }
}

@Immutable class Stretch {
    int lineNumber
    Coordinate a, b

    boolean intersects(Stretch s) {
        (ccw(  a,   b, s.a) * ccw(  a,   b, s.b) <= 0) &&
        (ccw(s.a, s.b,   a) * ccw(s.a, s.b,   b) <= 0)
    }

    BigDecimal ccw(Coordinate p, Coordinate q, Coordinate r) {
        (p.x * q.y - p.y * q.x) + (q.x * r.y - q.y * r.x) + (p.y * r.x - p.x * r.y)
    }
}

@Immutable class Coordinate {
    BigDecimal x, y
}