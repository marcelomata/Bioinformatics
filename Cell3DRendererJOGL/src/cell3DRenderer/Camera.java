package cell3DRenderer;

import mcib3d.geom.Point3D;

import com.jogamp.opengl.GL2;

public class Camera {
	
	private static final int CAMERA_ROTATE_STEP_DEGREES  = 2;
	
	private static final int MIN_ZOOM = -1380;
	private static final int MAX_ZOOM = -1;
	
	private static final float DEFAULT_CAMERA_ANGLE_X = 0.0f;
	private static final float DEFAULT_CAMERA_ANGLE_Y = 0.0f;
	private static final float DEFAULT_ZOOM = -138.0f;
//	private static final float DEFAULT_ZOOM = -208.0f;
//	private static final float DEFAULT_ZOOM = -408.0f;

	private Point3D cameraPosition;
	private Point3D initialPosition;
	private float cameraAngleX = DEFAULT_CAMERA_ANGLE_X;
	private float cameraAngleY = DEFAULT_CAMERA_ANGLE_Y;
	private float cameraAngleZ = 0.0f;
	private float zoom         = DEFAULT_ZOOM;
	
	private double fieldOfViewH;
	private double fieldOfViewV;
	
	public Camera() {
//		this.initialPosition = new Point3D(-100, -120, zoom);
//		this.initialPosition = new Point3D(-40, -50, zoom);
//		this.initialPosition = new Point3D(-20, -30, zoom);
		this.initialPosition = new Point3D(-10, -20, zoom);
		this.cameraPosition = new Point3D(initialPosition.getX(), initialPosition.getY(), initialPosition.getZ());
		this.fieldOfViewH = 90;
		this.fieldOfViewV = 60;
	}
	
	public void  draw(GL2 gl) {
		// camera transformations
		gl.glTranslatef((float)cameraPosition.getX(), (float)cameraPosition.getY(), (float)cameraPosition.getZ());
		gl.glRotatef(cameraAngleX, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(cameraAngleY, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(cameraAngleZ, 0.0f, 0.0f, 1.0f);
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

	public void rotateLeft() {
		cameraAngleY += CAMERA_ROTATE_STEP_DEGREES;
	}

	public void rotateRight() {
		cameraAngleY -= CAMERA_ROTATE_STEP_DEGREES;
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

	public void reset(Point3D maxPoint, Point3D minPoint) {
//		cameraAngleX = DEFAULT_CAMERA_ANGLE_X;
//		cameraAngleY = DEFAULT_CAMERA_ANGLE_Y;
		initPosition(maxPoint, minPoint);
		cameraAngleZ = 0.0f;
		cameraAngleX = DEFAULT_CAMERA_ANGLE_X;
		cameraAngleY = DEFAULT_CAMERA_ANGLE_Y;
//		zoom = DEFAULT_ZOOM;
//		cameraPosition.setX(initialPosition.getX());
//		cameraPosition.setY(initialPosition.getY());
//		cameraPosition.setZ(initialPosition.getZ());
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
	
	public void rotateQuaternion() {
		
	}

	public void initPosition(Point3D maxPoint, Point3D minPoint) {
		double ratio = 1.0/180.0;
		double z = ((Math.abs(maxPoint.getX()-minPoint.getX())/2) / Math.tan(((fieldOfViewH/2)*Math.PI)*ratio));
		z = -z - maxPoint.getZ() - 10;
		cameraPosition.setZ(z);
		zoom = (float) z;
		cameraPosition.setY(-((Math.abs(maxPoint.getY()-minPoint.getY())/2)+minPoint.getY()));
		cameraPosition.setX(-((Math.abs(maxPoint.getX()-minPoint.getX())/2)+minPoint.getX()));
		
//		this.initialPosition = new Point3D(-100, -120, zoom);
	}

	public double getFieldOfViewH() {
		return fieldOfViewH;
	}

	public double getFieldOfViewV() {
		return fieldOfViewV;
	}
	
	@Override
	public String toString() {
		String s = "";
		s += cameraPosition.getX()+", "+cameraPosition.getY()+", "+cameraPosition.getZ();
		return s;
	}
	
}
