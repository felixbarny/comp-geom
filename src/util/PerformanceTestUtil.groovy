package util

import groovy.transform.CompileStatic

@CompileStatic
final class PerformanceTestUtil {

	static benchmark(String message = "", int count = 1, int warmups = 0, Closure cl) {
		warmups.times { cl() }
		def startTime = System.currentTimeMillis()
		count.times { cl() }
		def deltaTime = System.currentTimeMillis() - startTime
		def average = deltaTime / count
		println((message ? "$message\t": "") +
				"time: $deltaTime ms" +
				(count > 1 ? "\tcount: $count \taverage: $average ms" : ""))
	}
}
