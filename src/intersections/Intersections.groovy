package intersections

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovyx.gpars.GParsPool

import java.util.concurrent.atomic.AtomicInteger;

GParsPool.withPool {
    new File('./').eachFileMatch(~/Strecken_(.*)\.dat/) { file ->
        println file.name
        def start = System.currentTimeMillis()

        def stretches = file.text.split('\n').collect { Stretch.valueOf(it.split()) }
        println "Reading file completed in ${System.currentTimeMillis() - start} ms"

        def count = new AtomicInteger()
        (0..<stretches.size()).eachParallel { int i1 ->
            (i1 + 1..<stretches.size()).each { int i2 ->
                if (stretches[i1].intersects(stretches[i2])) count.incrementAndGet()
            }
        }
        println "Found $count intersections"
        println "Calculating intersections of $file.name completed in ${System.currentTimeMillis() - start} ms\n\n"
    }
}

@Immutable class Stretch {
    Coordinate p, q

    static Stretch valueOf(points) {
        new Stretch(p: new Coordinate(x: points[0] as double, y: points[1] as double),
                    q: new Coordinate(x: points[2] as double, y: points[3] as double))
    }

    @CompileStatic boolean intersects(Stretch r) {
        def ccw1 = ccw(  p,   q, r.p) * ccw(  p,   q, r.q)
        def ccw2 = ccw(r.p, r.q, p) * ccw(r.p, r.q, q)

        if (ccw1 <= 0 && ccw2 <= 0) {
            if (ccw1 == 0 && ccw2 == 0) {
                def parallel = getLeftParallelStetch()
                if ((ccw(p, r.p, parallel.p) >= 0 && ccw(r.p, q, parallel.q) >= 0) || (ccw(p, r.q, parallel.p) >= 0 && ccw(r.q, q, parallel.q) >= 0)) {
                    if (isPoint() && r.isPoint()) this == r
                    else true
                } else false
            } else true
        } else false
    }

    @CompileStatic static double ccw(Coordinate p, Coordinate q, Coordinate r) {
        (p.x * q.y - p.y * q.x) + (q.x * r.y - q.y * r.x) + (p.y * r.x - p.x * r.y)
    }

    @CompileStatic Stretch getLeftParallelStetch() {
        def deltaX = q.x - p.x
        def deltaY = p.y - q.y
        valueOf([p.x+deltaY, p.y + deltaX, q.x + deltaY, q.y + deltaX])
    }

    @CompileStatic boolean isPoint() {
            q == p
        }

    String toString() {
        [p.x,p.y,q.x,q.y].toString()
    }

}

@Immutable @CompileStatic class Coordinate {


    double x, y
}
