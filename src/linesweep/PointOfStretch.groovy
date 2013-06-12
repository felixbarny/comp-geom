package linesweep

import groovy.transform.CompileStatic
import intersections.Point
import intersections.Stretch

@CompileStatic
class PointOfStretch extends Point {
    Stretch stretch;

    boolean isLeft() {
        stretch.p.x <= stretch.q.x && stretch.p.is(this)
    }

    boolean isRight() { !isLeft() }

}
