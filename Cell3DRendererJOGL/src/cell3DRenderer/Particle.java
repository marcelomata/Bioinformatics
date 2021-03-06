package cell3DRenderer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import mcib3d.geom.Object3D;
import mcib3d.geom.Point3D;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

public class Particle {
	
	private static final int MAX_DIAMETER = 5;
	
	private Object3D object;
	private Particle parent;
	private List<Particle> children;
	private Point3D position;
	private double diameter;
	private int track;
	private int frame;
	private Color color;
	
	public Particle(Object3D obj, int track, int frame, Color color) {
		this.object = obj;
		this.diameter = 3;
		if(obj != null) {
			this.position = obj.getCenterAsPoint();
			if(!Double.isNaN(getMaxAxisBoundBox(object))) {
				this.diameter = getMaxAxisBoundBox(object);
				this.diameter = diameter > MAX_DIAMETER ? MAX_DIAMETER : diameter;
			}
		}
		this.children = new ArrayList<Particle>();
		this.track = track;
		this.frame = frame;
		this.color = color;
	}
	
	private double getMaxAxisBoundBox(Object3D object) {
		double x = object.getXmax() - object.getXmin();
		double y = object.getYmax() - object.getYmin();
		double z = object.getZmax() - object.getZmin();
		double ret = (x > y ? (x > z ? x : z) : (y > z ? y : z) / 8);
		return ret > 20 ? 20 : ret;
	}

	public Point3D getPosition() {
		return position;
	}
	
	public void draw(GL2 gl, GLUT glut) {
		gl.glPushMatrix();
		float x = (float) position.getX();
		float y = (float) position.getY();
		float z = (float) position.getZ();
		draw2(gl, glut, x, y, z);
		gl.glPopMatrix();
		if(parent != null) {
			parent.draw(gl, glut);
			drawLine(gl, position, parent.getPosition(), color);
		}
	}
	
	private void draw2(GL2 gl, GLUT glut, float x, float y, float z) {
		gl.glTranslatef(x, y, z);
		gl.glColor3f((float)color.getRed()/255, (float)color.getGreen()/255, (float)color.getBlue()/255);
//		glut.glutSolidSphere(diameter, 10, 10);
		glut.glutSolidSphere(17, 10, 10);
	}
	
	private void drawLine(GL2 gl, Point3D p1, Point3D p2, Color c) {
		float x1 = (float) p1.getX();
		float y1 = (float) p1.getY();
		float z1 = (float) p1.getZ();
		float x2 = (float) p2.getX();
		float y2 = (float) p2.getY();
		float z2 = (float) p2.getZ();
		gl.glPointSize(2f);
		gl.glBegin(GL2.GL_LINES);
			gl.glColor3f((float)c.getRed()/255, (float)c.getGreen()/255, (float)c.getBlue()/255);
	      	gl.glVertex3f(x1, y1, z1);
	      	gl.glVertex3f(x2, y2, z2);
	    gl.glEnd();
	}
	
	public boolean isHidden() {
		return this.object == null;
	}
	
	public Particle getParent() {
		return parent;
	}
	
	public void setParent(Particle parent) {
		this.parent = parent;
	}
	
	public Object3D getObject() {
		return object;
	}
	
	public List<Particle> getChildren() {
		return children;
	}
	
	public void addChild(Particle child) {
		this.children.add(child);
	}
	
	public int getTrack() {
		return track;
	}

	public Color getColor() {
		return color;
	}
	
	public int getFrame() {
		return frame;
	}
}
