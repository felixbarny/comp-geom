package linesweep

import groovyx.gpars.GParsPool
import intersections.Point
import intersections.Stretch

GParsPool.withPool {
	new File('./').eachFileMatch(~/(.*)\.dat/) { file ->
		println file.name
		List<Stretch> stretches = file.readLines().collectParallel { Stretch.valueOf(it.split()) }
		TreeSet<Point> queue = new TreeSet<Point>({ a, b -> a.x <=> b.x ?: b.y <=> a.y } as Comparator<Point>)
		queue.addAll(stretches.p)
		queue.addAll(stretches.q)
	}
}


