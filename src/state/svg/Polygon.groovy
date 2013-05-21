package state.svg

import groovy.transform.CompileStatic
import intersections.Point
import intersections.Stretch

class Polygon {
	List<Point> points = [];

	private static final Random random = new Random()

	@CompileStatic
	boolean isPointInPolygon(Point pointToTest) {
		Stretch stretchToTestpoint = getStretchToTestpoint(pointToTest)
		int noOfIntersections = 0
		points.eachWithIndex { Point point, int i ->
			if (stretchToTestpoint.intersects(new Stretch(points[i - 1], point))) noOfIntersections++
		}
		noOfIntersections % 2 != 0
	}

	@CompileStatic
	private Stretch getStretchToTestpoint(Point pointToTest) {
		Stretch stretchToTestpoint = new Stretch(getPointOutsidePolygon(), pointToTest)
		// Point outside Polygon must not be colinear with any edge of the polygon
		while (points.any { Point point -> stretchToTestpoint.intersects(point.toStretch()) }) {
			def oldPointOutsidePolygon = stretchToTestpoint.p
			// If it is, try another one...
			def newPointOutsidePolygon = new Point(
					oldPointOutsidePolygon.x + random.nextDouble() * 5,
					oldPointOutsidePolygon.y + random.nextDouble() * 5)
			stretchToTestpoint = new Stretch(newPointOutsidePolygon, stretchToTestpoint.q)
		}
		return stretchToTestpoint
	}

	Point getPointOutsidePolygon() {
		new Point(points.x.max() + 2, points.y.max() + 2)
	}
}

