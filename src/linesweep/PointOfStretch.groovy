package linesweep

import groovy.transform.CompileStatic
import intersections.Point
import intersections.Stretch

@CompileStatic
class PointOfStretch extends Point {
	Stretch stretch;

	boolean isLeft() {
		if (stretch.p.is(this))
			stretch.p.x <= stretch.q.x
		else stretch.q.x <= stretch.p.x
	}

	boolean isRight() { !isLeft() }

	public static void main(String[] args) {
		def stretches = [Stretch.valueOf([2.921, 26.429, 2.9101, 26.2454]), Stretch.valueOf([11.774, 89.646, 11.662, 89.2365])]
		stretches.each { Stretch stretch ->
			assert stretch.q.isLeft()
			assert stretch.p.isRight()
		}
	}
}
