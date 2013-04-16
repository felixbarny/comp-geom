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
    Coordinate a, b

    static Stretch valueOf(points) {
        new Stretch(a: new Coordinate(x: points[0] as double, y: points[1] as double),
                    b: new Coordinate(x: points[2] as double, y: points[3] as double))
    }

    @CompileStatic boolean intersects(Stretch s) {
        (ccw(  a,   b, s.a) * ccw(  a,   b, s.b) <= 0) &&
        (ccw(s.a, s.b,   a) * ccw(s.a, s.b,   b) <= 0)
    }

    @CompileStatic static double ccw(Coordinate p, Coordinate q, Coordinate r) {
        (p.x * q.y - p.y * q.x) + (q.x * r.y - q.y * r.x) + (p.y * r.x - p.x * r.y)
    }
}

@Immutable @CompileStatic class Coordinate {
    double x, y
}
