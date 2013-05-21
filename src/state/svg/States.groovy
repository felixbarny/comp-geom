package state.svg

@Grab(group = 'org.apache.xmlgraphics', module = 'batik-parser', version = '1.7')
import groovy.transform.CompileStatic
import intersections.Point
import org.apache.batik.parser.AWTPathProducer
import org.apache.batik.parser.PathHandler
import org.apache.batik.parser.PathParser

import java.awt.*
import java.text.ParseException
import java.util.List

import static util.PerformanceTestUtil.benchmark

// souce: http://www.statistik-portal.de/Statistik-Portal/de_jb01_jahrtab1.asp
def sizeOfStates = [
		Baden__x26__Württemberg: 35751.48,
		Bayern: 70550.11,
		Berlin: 887.70,
		Brandenburg: 29483.13,
		Bremen: 419.24,
		Hamburg: 755.16,
		Hessen: 21114.91,
		'Mecklenburg-Vorpommern': 23190.76,
		Niedersachsen: 47612.88,
		'Nordrhein-Westfalen': 34092.25,
		'Rheinland-Pfalz': 19854.06,
		Saarland: 2568.75,
		Sachsen: 18419.71,
		'Sachsen-Anhalt': 20449.54,
		'Schleswig-Holstein': 15799.25,
		Thüringen: 16172.50,
]
benchmark {
	println "slurping svg...(mmh tasty).."
	def svg = new XmlSlurper().parse(new File("./states.svg"))

	println "\nextracting states..."
	Map<String, Shape> states = svg.g.path.findAll().collectEntries {
		[(it.@id as String): svgDataToShape(it.@d as String)]
	}

	List<State> polygonsOfStates = states.collect { State.valueOf(it.key, it.value) }

	def calculatedSizeOfStates = polygonsOfStates.collectEntries { [(it.name): it.getAreaInSqKm()] }
	calculatedSizeOfStates.each { println "Area of $it.key is ${it.value.round(2)} km2" }
	sizeOfStates.each { assert (it.value - calculatedSizeOfStates[it.key]).abs() < 260 }

	println "\nextracting cities..."
	Map<String, Point> cities = svg.path.findAll().collectEntries {
		[(it.@id): new Point(it.@"sodipodi:cx".text() as double, it.@"sodipodi:cy".text() as double)]
	}


	def citiesInStatesReference = states.collectEntries { state ->
		def citiesInState = cities.collect { city ->
			if (state.value.contains(city.value.x, city.value.y)) {
				return city.key
			} else return null
		}.findAll()
		[(state.key): citiesInState]
	}
	def citiesInStates = polygonsOfStates.collectEntries { state ->
		def citiesInState = cities.collect { city ->
			if (state.isCityInState(city.value)) {
				return city.key
			} else return null
		}.findAll()
		[(state.name): citiesInState]
	}
	citiesInStates.each { println "$it.key ist in ${it.value.join(', ')}" }
	assert citiesInStatesReference == citiesInStates
}

@CompileStatic
public Shape svgDataToShape(String s) throws ParseException {
	PathParser pp = new PathParser();
	PathHandler awtPathProducer = new AWTPathProducer()
	pp.setPathHandler(awtPathProducer);
	pp.parse(s);
	awtPathProducer.shape
}
