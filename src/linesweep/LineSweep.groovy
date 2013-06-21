package linesweep

import intersections.Point
import intersections.Stretch

SortedSet<Point> eventQueue
SortedSet<Stretch> sweepLine
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

	while (!eventQueue.isEmpty()) {
		def event = eventQueue.first()
//        println"eventQueue = "; eventQueue.each {
//            if (it instanceof PointOfStretch)
//        print "${it.left ? "S" : "E"}${stretches.indexOf(it.stretch) + 1} "
//            else if (it instanceof Intersection)
//        print "I${stretches.indexOf(it.stretch1) + 1},${stretches.indexOf(it.stretch2) + 1} "
//        }
//
//        println ""

		def oldSweepLine = sweepLine
		sweepLine = new TreeSet<Stretch>(
				{ Stretch a, Stretch b ->
					b.getYAt(event.x) <=> a.getYAt(event.x) ?: b.getYAt(event.x + 1) <=> a.getYAt(event.x + 1)
				} as Comparator)
		sweepLine.addAll(oldSweepLine)

		if (event instanceof PointOfStretch) {

//            print "event = ${event.left ? "S" : "E"}${stretches.indexOf(event.stretch) + 1}  "
//            def oldSweepLine = sweepLine
//            sweepLine = new TreeSet<Stretch>({ Stretch a, Stretch b -> b.getYAt(event.x) <=> a.getYAt(event.x) } as Comparator)
//            sweepLine.addAll(oldSweepLine)
			def segA = sweepLine.lower(event.stretch)
			def segB = sweepLine.higher(event.stretch)

//            print " segA = ${segA ? "L${stretches.indexOf(segA) + 1}" : "--"}"
//            print " segB = ${segB ? "L${stretches.indexOf(segB) + 1}" : "--"}"
			if (event.isLeft()) treatLeftEndpoint(event, segA, segB, sweepLine, eventQueue, intersections)
			else treatRightEndpoint(event, segA, segB, sweepLine, eventQueue, intersections)
		} else if (event instanceof Intersection) {

			def segA = sweepLine.lower(event.stretch2)
			def segB = sweepLine.higher(event.stretch1)

//            print " segA = ${segA ? "L${stretches.indexOf(segA) + 1}" : "--"}"
//            print " segB = ${segB ? "L${stretches.indexOf(segB) + 1}" : "--"}"
			treatIntersection(event as Intersection, segA, segB, intersections, eventQueue)
		}
		lastEvent = event
		eventQueue.remove(event)
//        print " sweepLine = " + sweepLine.collect { "L${stretches.indexOf(it) + 1}" }
//        println ""
//        println ""
	}

	println "found ${intersections.size()} intersections"
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

