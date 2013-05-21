package state.svg

import groovy.transform.CompileStatic
import intersections.Point

import java.awt.Shape
import java.awt.geom.PathIterator

class Polygon {
	List<Point> points = [];

	@CompileStatic
	static List<Polygon> valueOf(Shape stateShape) {
		def state = []
		Polygon polygonOfState = new Polygon()
		for (PathIterator pi = stateShape.getPathIterator(null); !pi.isDone(); pi.next()) {
			double[] coords = new double[2];
			int segmentType = pi.currentSegment(coords);
			if (!segmentType.equals(PathIterator.SEG_CLOSE)) {
				polygonOfState.points << new Point(coords[0], coords[1])
			} else {
				state << polygonOfState
				polygonOfState = new Polygon()
			}
		}
		return state
	}

	@CompileStatic
	boolean isHole(List<Polygon> state) {
		for (Polygon currentPolygon : state) {
			if (!this.is(currentPolygon)) {
				if (currentPolygon.isPointInPolygon(points.first()))
					true
			}
		}
		false
	}

	boolean isPointInPolygon(Point point) {
		def pointOutsidePolygon = getPointOutsidePolygon()
		// TODO
		false
	}

	Point getPointOutsidePolygon() {
		def maxX = Collections.max(points, { p1, p2 -> p1.x <=> p2.x } as Comparator<Point>)
		def maxY = Collections.max(points, { p1, p2 -> p1.y <=> p2.y } as Comparator<Point>)
		new Point(maxX.x + 2, maxY.y + 2)
	}
}
