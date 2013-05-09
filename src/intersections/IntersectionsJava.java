package intersections;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
            for (int index1 = 0; index1 < stretches.length; index1++) {
                final int finalIndex1 = index1;
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        int localCount = 0;
                        for (int index2 = finalIndex1 + 1; index2 < stretches.length; index2++) {
                            if (stretches[finalIndex1].intersects(stretches[index2])) {
                                localCount++;
                            }
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

