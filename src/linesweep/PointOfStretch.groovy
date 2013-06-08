package linesweep

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import intersections.Point
import intersections.Stretch

@Canonical
class PointOfStretch extends Point {
    Stretch stretch;

    @CompileStatic
    boolean isLeft() {
        stretch.p.x <= stretch.q.x && stretch.p.is(this)
    }

    @CompileStatic
    boolean isRight() { !isLeft() }

}
