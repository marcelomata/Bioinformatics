package cell3DRenderer;

import javax.media.opengl.GL2;

import mcib3d.geom.Point3D;

public class Camera {
	
	private static final int CAMERA_ROTATE_STEP_DEGREES  = 5;
	
	private static final int MIN_ZOOM = -980;
	private static final int MAX_ZOOM = -1;
	
	private static final float DEFAULT_CAMERA_ANGLE_X = 0.0f;
	private static final float DEFAULT_CAMERA_ANGLE_Y = 0.0f;
	private static final float DEFAULT_ZOOM = -408.0f;

	private Point3D cameraPosition;
	private Point3D initialPosition;
	private float cameraAngleX = DEFAULT_CAMERA_ANGLE_X;
	private float cameraAngleY = DEFAULT_CAMERA_ANGLE_Y;
	private float cameraAngleZ = 0.0f;
	private float zoom         = DEFAULT_ZOOM;
	
	public Camera() {
		this.initialPosition = new Point3D(-100, -120, zoom);
		this.cameraPosition = new Point3D(initialPosition.getX(), initialPosition.getY(), initialPosition.getZ());
	}
	
	public void  draw(GL2 gl) {
		
	}
	
	public Point3D getPosition() {
		return cameraPosition;
	}
	
	public float getAngleX() {
		return cameraAngleX;
	}
	
	public float getAngleY() {
		return cameraAngleY;
	}
	
	public float getAngleZ() {
		return cameraAngleZ;
	}

	public void rotateUp() {
		cameraAngleX -= CAMERA_ROTATE_STEP_DEGREES;
	}

	public void rotateDown() {
		cameraAngleX += CAMERA_ROTATE_STEP_DEGREES;	
	}

	public void rotateLeft(int orientation) {
		cameraAngleZ += CAMERA_ROTATE_STEP_DEGREES*orientation;
	}

	public void rotateRight(int orientation) {
		cameraAngleZ -= CAMERA_ROTATE_STEP_DEGREES*orientation;
	}

	public void changeX(int orientation) {
		cameraPosition.setX(cameraPosition.getX()+(10*orientation));
	}

	public void changeY(int orientation) {
		cameraPosition.setY(cameraPosition.getY()+(10*orientation));
	}

	public void changeZ(int orientation) {
		cameraPosition.setZ(cameraPosition.getZ()+(10*orientation));
	}

	public void reset() {
		cameraAngleX = DEFAULT_CAMERA_ANGLE_X;
		cameraAngleY = DEFAULT_CAMERA_ANGLE_Y;
		cameraAngleZ = 0.0f;
		zoom = DEFAULT_ZOOM;
		cameraPosition.setX(initialPosition.getX());
		cameraPosition.setY(initialPosition.getY());
		cameraPosition.setZ(initialPosition.getZ());
	}

	public void zoom(float orientation) {
		zoom += 10*orientation;
		if (zoom > MAX_ZOOM) zoom = MAX_ZOOM;
		if (zoom < MIN_ZOOM) zoom = MIN_ZOOM;
		cameraPosition.setZ(zoom);
	}

	public void rotateY(int orientation) {
		cameraAngleY += CAMERA_ROTATE_STEP_DEGREES*orientation;
	}

	public void rotateX(int orientation) {
		cameraAngleX += CAMERA_ROTATE_STEP_DEGREES*orientation;
	}
	
}
