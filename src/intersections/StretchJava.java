package intersections;

public class StretchJava {
    final Point p, q;

    public StretchJava(double[] points) {
        this.p = new Point(points[0], points[1]);
        this.q = new Point(points[2], points[3]);
    }

    public static StretchJava valueOf(double[] points) {
        return new StretchJava(points);
    }

    public boolean intersects(StretchJava r) {
        double ccw1 = ccw(p, q, r.p) * ccw(p, q, r.q);
        double ccw2 = ccw(r.p, r.q, p) * ccw(r.p, r.q, q);

        return ccw1 == 0 && ccw2 == 0 ? isOverlapping(r) : ccw1 <= 0 && ccw2 <= 0;
    }

    public boolean isOverlapping(StretchJava r) {
        StretchJava parallel = getLeftParallelStretch();
        return ((ccw(p, r.p, parallel.p) >= 0 && ccw(r.p, q, parallel.q) >= 0) ||
                (ccw(p, r.q, parallel.p) >= 0 && ccw(r.q, q, parallel.q) >= 0))
                && (!(isPoint() && r.isPoint()) || this.equals(r));
    }

    public static double ccw(Point p, Point q, Point r) {
        return (p.x * q.y - p.y * q.x) + (q.x * r.y - q.y * r.x) + (p.y * r.x - p.x * r.y);
    }

    public StretchJava getLeftParallelStretch() {
        double deltaX = q.x - p.x;
        double deltaY = p.y - q.y;
        return valueOf(new double[]{p.x + deltaY, p.y + deltaX, q.x + deltaY, q.y + deltaX});
    }

    public boolean isPoint() {
        return q.equals(p);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StretchJava that = (StretchJava) o;

        return p.equals(that.p) && q.equals(that.q);

    }

    @Override
    public int hashCode() {
        int result = p.hashCode();
        result = 31 * result + q.hashCode();
        return result;
    }
}
