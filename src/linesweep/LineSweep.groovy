package linesweep

import groovy.transform.Field
import intersections.Point
import intersections.Stretch

@Field NavigableSet<Point> eventQueue
@Field NavigableSet<PointOfStretch> sweepLine
@Field def intersections

new File('./').eachFileMatch(~/(.*)\.dat/) { file ->
    println file.name

    List<Stretch> stretches = file.readLines().collect { Stretch.valueOf(it.split()) }
    sweepLine = new TreeSet<PointOfStretch>({ a, b -> a.y <=> b.y ?: a.x <=> b.x } as Comparator<PointOfStretch>)
    eventQueue = new TreeSet<Point>({ a, b -> a.x <=> b.x ?: a.y <=> b.y } as Comparator<Point>)
    eventQueue.addAll(stretches.p)
    eventQueue.addAll(stretches.q)
    intersections = []

    eventQueue.each {
        if (it instanceof PointOfStretch) {
            if (it.isLeft()) {
                treatLeftEndpoint(it)
            } else {
                treatRightEndpoint(it)
            }
        } else {
            treatIntersection(it as Intersection)
        }
    }
}

void treatLeftEndpoint(PointOfStretch point) {
    sweepLine << point
    def segA = sweepLine.higher(point)
    def segB = sweepLine.lower(point)
    if (point.stretch.intersects(segA.stretch))
        eventQueue << Intersection.valueOf(point.stretch, segA.stretch)
    if (point.stretch.intersects(segB.stretch))
        eventQueue << Intersection.valueOf(point.stretch, segB.stretch)
}

void treatRightEndpoint(PointOfStretch point) {

}

void treatIntersection(Intersection intersection) {

}

