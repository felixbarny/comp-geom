package intersections

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovyx.gpars.GParsPool

import java.util.concurrent.atomic.AtomicInteger;

GParsPool.withPool {
    new File('./').eachFileMatch(~/Strecken_(.*)\.dat/) { file ->
        println file.name
        def start = System.currentTimeMillis()

        def stretches = file.readLines().collectParallel { Stretch.valueOf(it.split()) }
        println "Reading file completed in ${System.currentTimeMillis() - start} ms"

        def count = new AtomicInteger()
        (0..<stretches.size()).eachParallel { int i1 ->
                count.addAndGet(getCount(stretches, i1))
        }
        println "Found $count intersections"
        println "Calculating intersections of $file.name completed in ${System.currentTimeMillis() - start} ms\n\n"
    }
}

@CompileStatic def getCount(List<Stretch> stretches, int i1) {
    int count = 0
    for (int i2 = i1 + 1; i2 < stretches.size(); i2++) {
        if (stretches[i1].intersects(stretches[i2])) count++
    }
    return count
}

@Canonical class Stretch {
    Point p, q

    static Stretch valueOf(points) {
        Stretch stretch = new Stretch(p: new Point(x: points[0] as double, y: points[1] as double),
                    q: new Point(x: points[2] as double, y: points[3] as double))
		stretch.p.parent = stretch
		stretch.q.parent = stretch
    }

    @CompileStatic boolean intersects(Stretch r) {
        def ccw1 = ccw(  p,   q, r.p) * ccw(  p,   q, r.q)
        def ccw2 = ccw(r.p, r.q,   p) * ccw(r.p, r.q,   q)

        ccw1 == 0 && ccw2 == 0  ? isOverlpping(r) : ccw1 <= 0 && ccw2 <= 0
    }

    @CompileStatic boolean isOverlpping(Stretch r) {
        def parallel = getLeftParallelStretch()
        ((ccw(p, r.p, parallel.p) >= 0 && ccw(r.p, q, parallel.q) >= 0) ||
        (ccw(p, r.q, parallel.p) >= 0 && ccw(r.q, q, parallel.q) >= 0)) &&
        (!(isPoint() && r.isPoint()) || this == r)
    }

    @CompileStatic static double ccw(Point p, Point q, Point r) {
        (p.x * q.y - p.y * q.x) + (q.x * r.y - q.y * r.x) + (p.y * r.x - p.x * r.y)
    }

    @CompileStatic Stretch getLeftParallelStretch() {
        def deltaX = q.x - p.x
        def deltaY = p.y - q.y
        valueOf([p.x + deltaY, p.y + deltaX, q.x + deltaY, q.y + deltaX])
    }

    @CompileStatic boolean isPoint() { q == p }

    String toString() {
        [p.x, p.y, q.x, q.y].toString()
    }

}

@Canonical class Point {
    double x, y
	def parent
	Stretch toStretch() { new Stretch(this, this) }
}
