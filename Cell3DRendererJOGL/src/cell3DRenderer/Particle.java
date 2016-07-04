package cell3DRenderer;

import java.awt.Color;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;

import mcib3d.geom.Object3D;
import mcib3d.geom.Point3D;

public class Particle {
	
	private Object3D object;
	private Particle parent;
	private Point3D position;
	private double diameter;
	
	public Particle(Object3D obj) {
		this.object = obj;
		this.diameter = 3;
		if(obj != null) {
			this.position = obj.getCenterAsPoint();
			if(!Double.isNaN(object.getDistCenterMax())) {
				this.diameter = object.getDistCenterMax();
			}
		}
	}
	
	public Point3D getPosition() {
		return position;
	}
	
	public void draw(GL2 gl, GLUT glut, Color color) {
		gl.glPushMatrix();
		float x = (float) position.getX();
		float y = (float) position.getY();
		float z = (float) position.getZ();
		draw2(gl, glut, x, y, z, color);
		gl.glPopMatrix();
		if(parent != null) {
			parent.draw(gl, glut, Color.WHITE);
			drawLine(gl, position, parent.getPosition(), color);
		}
	}
	
	private void draw2(GL2 gl, GLUT glut, float x, float y, float z, Color color) {
		gl.glTranslatef(x, y, z);
		gl.glColor3f((float)color.getRed()/255, (float)color.getGreen()/255, (float)color.getBlue()/255);
		glut.glutSolidSphere(diameter*3, 10, 10);
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
}
