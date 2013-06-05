package linesweep

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import intersections.Point
import intersections.Stretch

@CompileStatic
@Canonical
class Intersection extends Point {
	Stretch stretch1, stretch2
}
