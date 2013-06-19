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
    stretches.findAll { !it.isVertical() }
    sweepLine = new TreeSet<>()
    eventQueue = new TreeSet<>({ a, b -> a.x <=> b.x ?: a.y <=> b.y } as Comparator<Point>)
    eventQueue.addAll(stretches.p)
    eventQueue.addAll(stretches.q)
    intersections = []

//    def lastEvent
    while (!eventQueue.isEmpty()) {
        def event = eventQueue.first()
//        if (lastEvent && event.x < lastEvent.x) {
//            eventQueue.remove(event)
//            continue
//        }
        //println"eventQueue = "; eventQueue.each {
//            if (it instanceof PointOfStretch)
        //print "${it.left ? "S" : "E"}${stretches.indexOf(it.stretch) + 1} "
//            else if (it instanceof Intersection)
        //print "I${stretches.indexOf(it.stretch1) + 1},${stretches.indexOf(it.stretch2) + 1} "
//        }

        //println ""

        if (event instanceof PointOfStretch) {

            //print "event = ${event.left ? "S" : "E"}${stretches.indexOf(event.stretch) + 1}  "
            def oldSweepLine = sweepLine
            sweepLine = new TreeSet<Stretch>({ Stretch a, Stretch b -> b.getYAt(event.x) <=> a.getYAt(event.x) } as Comparator)
            sweepLine.addAll(oldSweepLine)
            def segA = sweepLine.lower(event.stretch)
            def segB = sweepLine.higher(event.stretch)
            //print " segA = ${segA ? "L${stretches.indexOf(segA) + 1}" : "--"}"
            //print " segB = ${segB ? "L${stretches.indexOf(segB) + 1}" : "--"}"
            if (event.isLeft()) treatLeftEndpoint(event, segA, segB, sweepLine, eventQueue, intersections)
            else treatRightEndpoint(event, segA, segB, sweepLine, eventQueue, intersections)
        } else if (event instanceof Intersection) {

            //print "event = I${stretches.indexOf(event.stretch1) + 1},${stretches.indexOf(event.stretch2) + 1}"
            sweepLine = new ArrayList<>(sweepLine)
            // smaller index -> higher y
            def indexStretch1 = sweepLine.indexOf(event.stretch1)
            def indexStretch2 = sweepLine.indexOf(event.stretch2)
            Collections.swap(sweepLine, indexStretch1, indexStretch2)
            if (indexStretch2 < indexStretch1) event.swapStretches()

            def validIndices = 0..sweepLine.size() - 1
            def indexSegA = sweepLine.indexOf(event.stretch2) - 1
            def segA = validIndices.containsWithinBounds(indexSegA) ? sweepLine.get(indexSegA) : null
            def indexSegB = sweepLine.indexOf(event.stretch1) + 1
            def segB = validIndices.containsWithinBounds(indexSegB) ? sweepLine.get(indexSegB) : null
            //print " segA = ${segA ? "L${stretches.indexOf(segA) + 1}" : "--"}"
            //print " segB = ${segB ? "L${stretches.indexOf(segB) + 1}" : "--"}"
            treatIntersection(event as Intersection, segA, segB, intersections, eventQueue)
        }
        lastEvent = event
        eventQueue.remove(event)
        //print " sweepLine = " + sweepLine.collect { "L${stretches.indexOf(it) + 1}" }
        //println ""
        //println ""
    }

    println "found ${intersections.size()} intersections"
    println "found ${intersections.unique().size()} intersections"
}

void treatLeftEndpoint(PointOfStretch point, Stretch segA, Stretch segB, sweepLine, Set eventQueue, List intersections) {
    sweepLine << point.stretch
    if (point.stretch.intersects(segA))
        insertIntoEventQueueIfNotAlreadyFound(Intersection.valueOf(point.stretch, segA), eventQueue, intersections)

    if (point.stretch.intersects(segB))
        insertIntoEventQueueIfNotAlreadyFound(Intersection.valueOf(point.stretch, segB), eventQueue, intersections)
}

void treatRightEndpoint(PointOfStretch point, Stretch segA, Stretch segB, sweepLine, Set eventQueue, List intersections) {
    sweepLine.remove(point.stretch)
    if (segA?.intersects(segB))
        insertIntoEventQueueIfNotAlreadyFound(Intersection.valueOf(segA, segB), eventQueue, intersections)
}

void treatIntersection(Intersection intersection, Stretch segA, Stretch segB, List intersections, Set eventQueue) {
    intersections << intersection
    if (intersection.stretch2.intersects(segA))
        insertIntoEventQueueIfNotAlreadyFound(Intersection.valueOf(intersection.stretch2, segA), eventQueue, intersections)
    if (intersection.stretch1.intersects(segB))
        insertIntoEventQueueIfNotAlreadyFound(Intersection.valueOf(intersection.stretch1, segB), eventQueue, intersections)
}


void insertIntoEventQueueIfNotAlreadyFound(Intersection intersection, Set eventQueue, List intersections) {
    if (!intersections.contains(intersection) && !eventQueue.contains(intersection)) eventQueue << intersection
}

