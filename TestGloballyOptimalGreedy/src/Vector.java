import mcib3d.geom.Point3D;

public class Vector {

	private Point3D position;
	private double scale;
	private int frame;
	private Feature feature;
	
	public Vector(Point3D position, double scale, int frame) {
		this.position = position;
		this.scale = scale;
		this.frame = frame;
	}
	
	public Feature getFeature() {
		return feature;
	}
	
}
