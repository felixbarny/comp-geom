package state.svg

@Grab(group='org.apache.xmlgraphics', module='batik-parser', version='1.7')
import intersections.Coordinate
import org.apache.batik.parser.DefaultPointsHandler
import org.apache.batik.parser.PointsParser
import org.apache.batik.parser.ParseException
import org.apache.batik.parser.PointsHandler


//def svg = new XmlSlurper().parse(new File("./states.svg"))

//println svg.g.path.@id
def list = extractPoints(new File("./states.svg").text)
println list

class Polygon {
//    List<Point> points
}

public List extractPoints(String s) throws ParseException {
       final LinkedList points = new LinkedList();
       PointsParser pp = new PointsParser();
       PointsHandler ph = new DefaultPointsHandler() {
           public void point(float x, float y) throws ParseException {
               points << new Coordinate(x, y)
           }
       };
       pp.setPointsHandler(ph);
       pp.parse(s);
       return points;
   }


