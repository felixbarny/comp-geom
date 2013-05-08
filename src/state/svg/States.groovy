package state.svg

import groovy.transform.CompileStatic
@Grab(group = 'org.apache.xmlgraphics', module = 'batik-parser', version = '1.7')
import org.apache.batik.parser.AWTPathProducer
import org.apache.batik.parser.ParseException
import org.apache.batik.parser.PathHandler
import org.apache.batik.parser.PathParser

import java.awt.*
import java.awt.geom.PathIterator
import java.util.List

println "slurping svg..."
def svg = new XmlSlurper().parse(new File("./states.svg"))

println "extracting states..."
Map<String, Shape> states = svg.g.path.findAll().collectEntries {
    [(it.@id): svgDataToShape(it.@d as String)]
}

def statePaths = states.collectEntries {
    def path = []
    for (PathIterator pi = it.value.getPathIterator(null); !pi.isDone(); pi.next()) {
        double[] coords = new double[2];
        int segmentType = pi.currentSegment(coords);
        if (!segmentType.equals(PathIterator.SEG_CLOSE)) {
            path << coords
        }
    }
    [(it.key): path]
}

statePaths.each {
    println "Area of $it.key=${getArea(it.value).toBigDecimal().toPlainString()}"
}

println "extracting cities..."
def cities = svg.path.findAll().collectEntries {
    [(it.@id): [it.@"sodipodi:cx".text() as double, it.@"sodipodi:cy".text() as double]]
}

states.each { state ->
    cities.each { city ->
        if (state.value.contains(city.value[0], city.value[1])) {
            println "$city.key ist in $state.key"
        }
    }
}

public Shape svgDataToShape(String s) throws ParseException {
    PathParser pp = new PathParser();
    PathHandler awtPathProducer = new AWTPathProducer()
    pp.setPathHandler(awtPathProducer);
    pp.parse(s);
    awtPathProducer.shape
}

@CompileStatic
public double getArea(List<double[]> polygon) {
    double sum = 0
    for (int i = 0; i < polygon.size() - 1; i++) {
        sum += ((double) polygon[i][1]) * (polygon[i - 1][0] + polygon[i + 1][0]) / 2;
    }
    return sum
}
