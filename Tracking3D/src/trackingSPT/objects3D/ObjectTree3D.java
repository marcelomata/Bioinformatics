package trackingSPT.objects3D;

import java.util.ArrayList;
import java.util.List;

import mcib3d.geom.Object3D;
import mcib3d.geom.Point3D;
import mcib3d.geom.Vector3D;

public class ObjectTree3D {
	
	private Object3D object;
	private ObjectTree3D parent;
	private List<ObjectTree3D> children;
	private Integer id;
	private double velocity;
	private double acceleration;
	private double area;
	private double areaDifference;
	private int region;
	private Vector3D orientation;
	private int frame;
	
	public ObjectTree3D(Object3D object, int frame) {
		this.object = object;
		this.parent = null;
		this.children = new ArrayList<ObjectTree3D>();
		this.id = -1;
		this.orientation = new Vector3D(0, 0, 0);
		this.velocity = 0;
		this.acceleration = 0;
		this.area = object.getAreaPixels();
		this.areaDifference = 0;
		this.velocity = 0;
		this.frame = frame;
	}
	
	public Object3D getObject() {
		return object;
	}
	
	public ObjectTree3D getParent() {
		return parent;
	}
	
	public List<ObjectTree3D> getChildren() {
		return children;
	}
	
	public List<Object3D> getChildrenObject3D() {
		List<Object3D> childrenObject3D = new ArrayList<Object3D>();
		for (ObjectTree3D objectTree : children) {
			childrenObject3D.add(objectTree.getObject());
		}
		return childrenObject3D;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}
	
	public boolean isMissed() {
		return object == null;
	}
	
	public boolean isRoot() {
		return this.parent == null;
	}
	
	public void setParent(ObjectTree3D parent) {
		this.parent = parent;
	}

	public void addChild(ObjectTree3D child) {
		this.children.add(child);
	}
	
	public int getFrame() {
		return frame;
	}

	public void removeChildren() {
		this.children.clear();
	}
	
	public void updateAtributes() {
		updateAcceleration();
		updateArea();
		updateAreaDifference();
		updateOrientation();
		updateRegion();
	}
	
	private void updateRegion() {
		region = 0;
	}

	private void updateOrientation() {
		Vector3D lastOrientation = this.parent.getOrientation();
		this.orientation = new Vector3D(lastOrientation, this.object.getCenterAsPoint());
		this.orientation.normalize();
	}

	private void updateAreaDifference() {
		this.areaDifference = this.areaDifference - parent.getArea();
	}

	private void updateArea() {
		this.area = this.object.getAreaPixels();
	}

	private void updateAcceleration() {
		updateVelocity();
		this.acceleration = parent.getVelocity() - this.velocity;
	}

	private void updateVelocity() {
		Point3D lastPosition = parent.getObject().getCenterAsPoint();
		Point3D thisPosition = this.object.getCenterAsPoint();
		this.velocity = thisPosition.distance(lastPosition);
	}
	
	public int getRegion() {
		return region;
	}
	
	public Vector3D getOrientation() {
		return orientation;
	}
	
	public double getArea() {
		return area;
	}
	
	public double getAreaDifference() {
		return areaDifference;
	}
	
	public double getVelocity() {
		return velocity;
	}
	
	public double getAcceleration() {
		return acceleration;
	}

	@Override
	public String toString() {
		return String.valueOf(id)+" - P -> "+object.getCenterAsPoint();
	}
}
