package linesweep

import groovyx.gpars.GParsPool
import intersections.Point
import intersections.Stretch

GParsPool.withPool {
	new File('./').eachFileMatch(~/(.*)\.dat/) { file ->
		println file.name
		List<Stretch> stretches = file.readLines().collectParallel { Stretch.valueOf(it.split()) }
		TreeSet<Point> queue = new TreeSet<Point>({ a, b -> a.x <=> b.x ?: a.y <=> b.y } as Comparator<Point>)
		queue.addAll(stretches.p)
		queue.addAll(stretches.q)
		TreeSet<Point> sweepLine = new TreeSet<Point>({ a, b -> a.x <=> b.x ?: b.y <=> a.y } as Comparator<Point>)
		def intersections = []

		queue.each {
			if (it.isLeft()) {
			 	treatLeftEndpoint(it)
			}
			if (it.isRight()) {
				treatRightEndpoint(it)
			}
			if (it instanceof Intersection) {
				treatIntersection(it)
			}
		}
	}
}

void treatLeftEndpoint(Point point) {}

void treatRightEndpoint(Point point) {}

void treatIntersection(Intersection intersection) {}