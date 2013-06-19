package state.svg

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import intersections.Point
import state.svg.Polygon

import java.awt.*
import java.awt.geom.PathIterator
import java.util.List

@Immutable
class State {
    String name
    List<Polygon> polygonsOfState

    @CompileStatic
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

    @CompileStatic
    double getAreaInSqKm() {
        double sum = 0
        polygonsOfState.eachWithIndex { Polygon polygon, int i1 ->
            def sumPolygon = 0
            for (int i = -1; i < polygon.points.size() - 1; i++) {
                sumPolygon += ((double) polygon.points[i].y) *
                        (polygon.points[i - 1].x - polygon.points[i + 1].x) / 2;
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

    @CompileStatic
    boolean isCityInState(Point city) {
        boolean result = false
        if (isInBoundingBox(city)) {
            for (Polygon polygon : polygonsOfState) {
                if (polygon.isPointInPolygon(city)) {
                    // if city is in two polygons of state
                    // then second polygon is hole
                    // (Berlin is not in Brandenburg)
                    result = !result
                }
            }
        }
        return result
    }

    @CompileStatic
    boolean isInBoundingBox(Point point) {
        def allX = polygonsOfState*.points*.x.flatten()
        def allY = polygonsOfState*.points*.y.flatten()
        return (allX.min()..allX.max()).containsWithinBounds(point.x) &&
                (allY.min()..allY.max()).containsWithinBounds(point.y)
    }

    @CompileStatic
    boolean isHole(Polygon polygon) {
        for (Polygon currentPolygon : polygonsOfState) {
            if (!polygon.is(currentPolygon)) {
                if (currentPolygon.isPointInPolygon(polygon.points.first())) {
                    return true
                }
            }
        }
        return false
    }
}
