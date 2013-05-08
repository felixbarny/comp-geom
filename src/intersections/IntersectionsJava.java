package intersections;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class IntersectionsJava {

    public static void main(String[] args) throws Exception {
        for (String fileName : Arrays.asList("Strecken_1000.dat", "Strecken_10000.dat", "Strecken_100000.dat")) {
            System.out.println(fileName);
            long start = System.currentTimeMillis();
            final StretchJava[] stretches = readStretches(fileName);
            System.out.println("Reading file completed in " + (System.currentTimeMillis() - start) + " ms");

            ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
            final AtomicInteger count = new AtomicInteger();
            for (int i1 = 0; i1 < stretches.length; i1++) {
                final int finalI = i1;
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        int localCount = 0;
                        for (int i2 = finalI + 1; i2 < stretches.length; i2++) {
                            if (stretches[finalI].intersects(stretches[i2])) localCount++;
                        }
                        count.getAndAdd(localCount);
                    }
                });
            }
            pool.shutdown();
            pool.awaitTermination(1, TimeUnit.HOURS);

            System.out.println("count = " + count);
            System.out.println("Calculating took " + (System.currentTimeMillis() - start) + " ms\n\n");
        }
    }

    private static StretchJava[] readStretches(String fileName) throws IOException {
        try (BufferedReader bufferedReader =
                     new BufferedReader(new FileReader("/Users/najum/Documents/uni/ComputationalGeometry/comp-geom/src/intersections/" + fileName))) {
            List<StretchJava> stretches = new ArrayList<StretchJava>(10000);
            String line = bufferedReader.readLine();

            while (line != null) {
                String[] split = line.split(" ");
                double[] doubles = new double[split.length];
                for (int i = 0; i < split.length; i++) {
                    String s = split[i];
                    doubles[i] = Double.valueOf(s);
                }
                stretches.add(StretchJava.valueOf(doubles));
                line = bufferedReader.readLine();
            }
            return stretches.toArray(new StretchJava[stretches.size()]);
        }
    }
}

class StretchJava {
    CoordinateJava p, q;

    static StretchJava valueOf(double[] points) {
        StretchJava stretch = new StretchJava();
        stretch.p = new CoordinateJava(points[0], points[1]);
        stretch.q = new CoordinateJava(points[2], points[3]);
        return stretch;
    }

    boolean intersects(StretchJava r) {
        double ccw1 = ccw(p, q, r.p) * ccw(p, q, r.q);
        double ccw2 = ccw(r.p, r.q, p) * ccw(r.p, r.q, q);

        return ccw1 == 0 && ccw2 == 0 ? isOverlpping(r) : ccw1 <= 0 && ccw2 <= 0;
    }

    boolean isOverlpping(StretchJava r) {
        StretchJava parallel = getLeftParallelStretch();
        return ((ccw(p, r.p, parallel.p) >= 0 && ccw(r.p, q, parallel.q) >= 0) ||
                (ccw(p, r.q, parallel.p) >= 0 && ccw(r.q, q, parallel.q) >= 0))
                && (!(isPoint() && r.isPoint()) || this == r);
    }

    static double ccw(CoordinateJava p, CoordinateJava q, CoordinateJava r) {
        return (p.x * q.y - p.y * q.x) + (q.x * r.y - q.y * r.x) + (p.y * r.x - p.x * r.y);
    }

    StretchJava getLeftParallelStretch() {
        double deltaX = q.x - p.x;
        double deltaY = p.y - q.y;
        return valueOf(new double[]{p.x + deltaY, p.y + deltaX, q.x + deltaY, q.y + deltaX});
    }

    boolean isPoint() {
        return q.equals(p);
    }
}

class CoordinateJava {
    double x, y;

    CoordinateJava(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
