package linesweep

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import intersections.Point
import intersections.Stretch

@CompileStatic
@Canonical
class Intersection extends Point {
	Stretch stretch1, stretch2

    private Intersection(double x, double y, Stretch stretch1, Stretch stretch2) {
        super(x, y)
        this.stretch1 = stretch1
        this.stretch2 = stretch2
    }

    static Intersection valueOf(Stretch stretch1, Stretch stretch2) {


        new Intersection(x, y, stretch1, stretch2)
    }


}
