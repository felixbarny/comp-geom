package linesweep

import intersections.Point
import intersections.Stretch

SortedSet<Point> eventQueue
Collection<Stretch> sweepLine
def intersections

new File('./').eachFileMatch(~/(.*)\.dat/) { file ->
//new File('./').eachFileMatch(~/(.*)test\.dat/) { file ->
    println file.name

    List<Stretch> stretches = file.readLines().collect { Stretch.valueOf(it.split()) }
    sweepLine = new TreeSet<>()
    eventQueue = new TreeSet<>({ a, b -> a.x <=> b.x ?: a.y <=> b.y } as Comparator<Point>)
    eventQueue.addAll(stretches.p)
    eventQueue.addAll(stretches.q)
    intersections = []

    while (!eventQueue.isEmpty()) {
        def event = eventQueue.first()
//        println "eventQueue = $eventQueue"
//        print "event = $event"

        if (event instanceof PointOfStretch) {
            def oldSweepLine = sweepLine
            sweepLine = new TreeSet<Stretch>({ Stretch a, Stretch b -> b.getYAt(event.x) <=> a.getYAt(event.x) } as Comparator)
            sweepLine.addAll(oldSweepLine)
            def segA = sweepLine.lower(event.stretch)
            def segB = sweepLine.higher(event.stretch)
//            print "segA = $segA"
//            print "segB = $segB"
            if (event.isLeft()) treatLeftEndpoint(event, segA, segB, sweepLine, eventQueue)
            else treatRightEndpoint(event, segA, segB, sweepLine, eventQueue)
        } else if (event instanceof Intersection) {
            sweepLine = new ArrayList<>(sweepLine)
            Collections.swap(sweepLine, sweepLine.indexOf(event.stretch1), sweepLine.indexOf(event.stretch2))
//            PointOfStretch pseudo = new PointOfStretch(x: event.x, y: event.y, stretch: event.stretch1)
//            PointOfStretch pseudo2 = new PointOfStretch(x: event.x, y: event.y, stretch: event.stretch2)
            def validIndices = 0..sweepLine.size() - 1
            def indexSegA = sweepLine.indexOf(event.stretch2) - 1
            def segA = validIndices.containsWithinBounds(indexSegA) ? sweepLine.get(indexSegA) : null
            def indexSegB = sweepLine.indexOf(event.stretch1) + 1
            def segB = validIndices.containsWithinBounds(indexSegB) ? sweepLine.get(indexSegB) : null
//            def segB = sweepLine.higher(pseudo)
//            print "segA = $segA"
//            print "segB = $segB"
            treatIntersection(event as Intersection, segA, segB, intersections, eventQueue)
        }
        eventQueue.remove(event)
//        print " sweepLine = $sweepLine"
//        println ""
//        println ""

    }
    println "found ${intersections.size()} intersections"
}

void treatLeftEndpoint(PointOfStretch point, Stretch segA, Stretch segB, sweepLine, eventQueue) {
    sweepLine << point.stretch
    if (point.stretch.intersects(segA) && !point.stretch.is(segA))
        eventQueue << Intersection.valueOf(point.stretch, segA)
    if (point.stretch.intersects(segB) && !point.stretch.is(segB))
        eventQueue << Intersection.valueOf(point.stretch, segB)
}

void treatRightEndpoint(PointOfStretch point, Stretch segA, Stretch segB, sweepLine, eventQueue) {
    sweepLine.remove(point.stretch)
    if (segA?.intersects(segB))
        eventQueue << Intersection.valueOf(segA, segB)
}

void treatIntersection(Intersection intersection, Stretch segA, Stretch segB, intersections, eventQueue) {
    intersections << intersection
    if (intersection.stretch2.intersects(segA) && !intersection.stretch2.is(segA))
        eventQueue << Intersection.valueOf(intersection.stretch2, segA)
    if (intersection.stretch1.intersects(segB) && !intersection.stretch1.is(segB))
        eventQueue << Intersection.valueOf(intersection.stretch1, segB)
}

