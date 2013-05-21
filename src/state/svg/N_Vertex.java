package state.svg;

import javax.swing.*;
import java.awt.*;
import java.awt.Polygon;

public class N_Vertex extends JPanel {
	final Shape shape;

	public N_Vertex(Shape shape) {
		this.shape = shape;
	}

	@Override
	protected void paintComponent(Graphics g) {
		((Graphics2D) g).draw(shape);
	}

	public static void paintShape(Shape polygon) {
		if (polygon == null) return;
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.add(new N_Vertex(polygon));
		f.setSize(200, 200);
		f.setVisible(true);
	}

	public static void main(String[] args) {
		Polygon polygon = new Polygon();
		polygon.addPoint(0, 0);
		polygon.addPoint(20, 0);
		polygon.addPoint(10, 20);
		paintShape(polygon);
	}
}