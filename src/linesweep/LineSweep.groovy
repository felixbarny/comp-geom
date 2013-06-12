package linesweep

import intersections.Point
import intersections.Stretch

SortedSet<Point> eventQueue
SortedSet<PointOfStretch> sweepLine
def intersections

new File('./').eachFileMatch(~/(.*)\.dat/) { file ->
	println file.name

	List<Stretch> stretches = file.readLines().collect { Stretch.valueOf(it.split()) }
	sweepLine = new TreeSet<>()
	eventQueue = new TreeSet<>({ a, b -> a.x <=> b.x ?: a.y <=> b.y } as Comparator<Point>)
	eventQueue.addAll(stretches.p)
	eventQueue.addAll(stretches.q)
	intersections = []

	while (!eventQueue.isEmpty()) {
		def event = eventQueue.first()
		def oldSweepLine = sweepLine
		sweepLine = new TreeSet<PointOfStretch>(
				{ PointOfStretch a, PointOfStretch b ->
					b.stretch.getYAt(event.x) <=> a.stretch.getYAt(event.x) ?:
						b.stretch.getYAt(event.x + 1) <=> a.stretch.getYAt(event.x + 1)
				} as Comparator)
		sweepLine.addAll(oldSweepLine)

		if (event instanceof PointOfStretch) {
			def segA = sweepLine.higher(event)
			def segB = sweepLine.lower(event)
			if (event.isLeft()) treatLeftEndpoint(event, segA, segB, sweepLine, eventQueue)
			else treatRightEndpoint(event, segA, segB, sweepLine, eventQueue)
		} else if (event instanceof Intersection){
			PointOfStretch pseudo = new PointOfStretch(x: event.x, y: event.y, stretch: event.stretch1)
			def segB = sweepLine.higher(pseudo)
			def segA = sweepLine.lower(pseudo)
			treatIntersection(event as Intersection, segA, segB, intersections, eventQueue)
		}
		eventQueue.remove(event)
	}
	println "found ${intersections.size()} intersections"
}

void treatLeftEndpoint(PointOfStretch point, PointOfStretch segA, PointOfStretch segB, sweepLine, eventQueue) {
	sweepLine << point
	if (point.stretch.intersects(segA?.stretch) && !point.stretch.is(segA.stretch))
		eventQueue << Intersection.valueOf(point.stretch, segA.stretch)
	if (point.stretch.intersects(segB?.stretch) && !point.stretch.is(segB.stretch))
		eventQueue << Intersection.valueOf(point.stretch, segB.stretch)
}

void treatRightEndpoint(PointOfStretch point, PointOfStretch segA, PointOfStretch segB, sweepLine, eventQueue) {
	sweepLine.remove(point)
	if (segA?.stretch?.intersects(segB?.stretch))
		eventQueue << Intersection.valueOf(segA.stretch, segB.stretch)
}

void treatIntersection(Intersection intersection, PointOfStretch segA, PointOfStretch segB, intersections, eventQueue) {
	intersections << intersection
	if (intersection.stretch2.intersects(segA?.stretch) && !intersection.stretch2.is(segA.stretch))
		eventQueue << Intersection.valueOf(intersection.stretch2, segA.stretch)
	if (intersection.stretch1.intersects(segB?.stretch) && !intersection.stretch1.is(segB.stretch))
		eventQueue << Intersection.valueOf(intersection.stretch1, segB.stretch)
}

