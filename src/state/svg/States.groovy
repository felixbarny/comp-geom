package state.svg

@Grab(group = 'org.apache.xmlgraphics', module = 'batik-parser', version = '1.7')
import groovy.transform.CompileStatic
import org.apache.batik.parser.AWTPathProducer
import org.apache.batik.parser.PathHandler
import org.apache.batik.parser.PathParser

import java.awt.*
import java.text.ParseException
import java.util.List

println "slurping svg...(mmh tasty).."
def svg = new XmlSlurper().parse(new File("./states.svg"))

println "\n extracting states..."
Map<String, Shape> states = svg.g.path.findAll().collectEntries {
	[(it.@id): svgDataToShape(it.@d as String)]
}

def polygonsOfStates = states.collectEntries {
	[(it.key): Polygon.valueOf(it.value)]
}

polygonsOfStates.each {
	println "Area of $it.key is ${getAreaInSqKm(it.value).round(2)} km2"
}

println "\n extracting cities..."
Map<String, intersections.Point> cities = svg.path.findAll().collectEntries {
	[(it.@id): new intersections.Point(it.@"sodipodi:cx".text() as double, it.@"sodipodi:cy".text() as double)]
}

states.each { state ->
	cities.each { city ->
		if (state.value.contains(city.value.x, city.value.y)) {
			println "$city.key ist in $state.key"
		}
	}
}

@CompileStatic
public Shape svgDataToShape(String s) throws ParseException {
	PathParser pp = new PathParser();
	PathHandler awtPathProducer = new AWTPathProducer()
	pp.setPathHandler(awtPathProducer);
	pp.parse(s);
	awtPathProducer.shape
}



@CompileStatic
public double getAreaInSqKm(List<Polygon> state) {
	double sum = 0
	state.eachWithIndex { Polygon polygon, int i1 ->
		def sumPolygon = 0
		for (int i = -1; i < polygon.points.size() - 1; i++) {
			sumPolygon += ((double) polygon.points[i].y) * (polygon.points[i - 1].x - polygon.points[i + 1].x) / 2;
		}
		sumPolygon = sumPolygon.abs()
//        if (polygon.isHole(state)) {
//            sum -= sumPolygon
//        } else {
		sum += sumPolygon
//        }
	}
	// factor to convert result in actual km²
	return sum / 0.85d
}
