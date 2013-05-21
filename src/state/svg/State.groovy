package state.svg

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import intersections.Point

import java.awt.*
import java.awt.geom.PathIterator
import java.util.List

@Immutable
class State {
	String name
	List<Polygon> poygonsOfState

	static State valueOf(String name, Shape stateShape) {
		List<Polygon> state = []
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
		return new State(name, state)
	}

	double getAreaInSqKm() {
		double sum = 0
		poygonsOfState.eachWithIndex { Polygon polygon, int i1 ->
			def sumPolygon = 0
			for (int i = -1; i < polygon.points.size() - 1; i++) {
				sumPolygon += ((double) polygon.points[i].y) * (polygon.points[i - 1].x - polygon.points[i + 1].x) / 2;
			}
			sumPolygon = sumPolygon.abs()
			if (isHole(polygon)) {
				sum -= sumPolygon
			} else {
				sum += sumPolygon
			}
		}
		// factor to convert result in actual km²
		return sum / 0.85d
	}

	boolean isCityInState(Point city) {
		boolean result = false
		for (Polygon polygon : poygonsOfState) {
			if (polygon.isPointInPolygon(city)) {
				// if city is in two polygons of state -> second polygon is hole
				// (Berlin is not in Brandenburg)
				result = !result
			}
		}
		return result
	}

	@CompileStatic
	boolean isHole(Polygon polygon) {
		for (Polygon currentPolygon : poygonsOfState) {
			if (!polygon.is(currentPolygon)) {
				if (currentPolygon.isPointInPolygon(polygon.points.first()))
					true
			}
		}
		false
	}
}
