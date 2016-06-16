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
	
	public Particle(Object3D obj) {
		this.object = obj;
		if(obj != null) {
			this.position = obj.getCenterAsPoint();
		}
	}
	
	public Point3D getPosition() {
		return position;
	}
	
	public void draw(GL2 gl, GLUT glut, Point3D minPosition, Color color) {
		gl.glPushMatrix();
		float x = (float) (position.getX() - minPosition.getX());
		float y = (float) (position.getY() - minPosition.getY());
		float z = (float) (position.getZ() - minPosition.getZ());
		draw2(gl, glut, x, y, z, object.getDistCenterMax(), color);
		gl.glPopMatrix();
		if(parent != null) {
			parent.draw(gl, glut, minPosition, Color.WHITE);
			drawLine(gl, position, parent.getPosition(), minPosition, color);
		}
	}
	
	private void draw2(GL2 gl, GLUT glut, float x, float y, float z, double diameter, Color color) {
		gl.glTranslatef(x, y, z);
		gl.glColor3f((float)color.getRed()/255, (float)color.getGreen()/255, (float)color.getBlue()/255);
		glut.glutSolidSphere(diameter, 10, 10);
	}
	
	private void drawLine(GL2 gl, Point3D p1, Point3D p2, Point3D translate, Color c) {
		float x1 = (float) (p1.getX() - translate.getX());
		float y1 = (float) (p1.getY() - translate.getY());
		float z1 = (float) (p1.getZ() - translate.getZ());
		float x2 = (float) (p2.getX() - translate.getX());
		float y2 = (float) (p2.getY() - translate.getY());
		float z2 = (float) (p2.getZ() - translate.getZ());
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
}
