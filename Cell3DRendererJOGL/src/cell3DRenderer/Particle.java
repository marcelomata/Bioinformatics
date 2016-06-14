package cell3DRenderer;

import java.awt.Color;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;

import mcib3d.geom.Object3D;
import mcib3d.geom.Point3D;

public class Particle {
	
	private Object3D object;
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
		gl.glTranslatef(x, y, z);
		gl.glColor3f((float)color.getRed()/255, (float)color.getGreen()/255, (float)color.getBlue()/255);
		glut.glutSolidSphere(2, 10, 10);
		gl.glPopMatrix();
	}
	
	public boolean isHidden() {
		return this.object == null;
	}

}
