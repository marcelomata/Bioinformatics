package trackingSPT.objects3D;

import java.util.ArrayList;
import java.util.List;

import mcib3d.geom.Object3D;
import mcib3d.geom.Vector3D;

public class ObjectTree3D {
	
	private Object3D object;
	private ObjectTree3D parent;
	private List<ObjectTree3D> children;
	private Integer id;
	private double velocity;
	private double acceleration;
	private Vector3D orientation;
	private double area;
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
		
	}
	
	@Override
	public String toString() {
		return String.valueOf(id)+" - P -> "+object.getCenterAsPoint();
	}
}
