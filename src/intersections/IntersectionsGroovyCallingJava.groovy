package intersections

import groovy.transform.CompileStatic
import groovyx.gpars.GParsPool

import java.util.concurrent.atomic.AtomicInteger;

GParsPool.withPool {
    new File('./').eachFileMatch(~/Strecken_(.*)\.dat/) { file ->
        println file.name
        def start = System.currentTimeMillis()

        def stretchesList = file.readLines().collect { StretchJava.valueOf(it.split().collect{it as double}.toArray(new double[4])) }
        def stretches = stretchesList.toArray(new StretchJava[stretchesList.size()])
        println "Reading file completed in ${System.currentTimeMillis() - start} ms"

        def count = new AtomicInteger()
        (0..<stretches.size()).eachParallel { int i1 ->
                count.addAndGet(getCount(stretches, i1))
        }
        println "Found $count intersections"
        println "Calculating intersections of $file.name completed in ${System.currentTimeMillis() - start} ms\n\n"
    }
}

@CompileStatic def getCount(StretchJava[] stretches, int i1) {
    int count = 0
    for (int i2 = i1 + 1; i2 < stretches.size(); i2++) {
        if (stretches[i1].intersects(stretches[i2])) count++
    }
    return count
}
