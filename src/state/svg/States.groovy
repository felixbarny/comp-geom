package state.svg

@Grab(group = 'org.apache.xmlgraphics', module = 'batik-parser', version = '1.7')
import org.apache.batik.parser.AWTPathProducer
import org.apache.batik.parser.ParseException
import org.apache.batik.parser.PathHandler
import org.apache.batik.parser.PathParser

import java.awt.*

println "slurping svg..."
def svg = new XmlSlurper().parse(new File("./states.svg"))

println "extracting polygons..."
Map<String, Shape> states = svg.g.path.findAll().collectEntries {
    [(it.@id): svgDataToShape(it.@d as String)]
}
states.each { N_Vertex.paintShape(it.value) }

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
