package trackingSPT.objects;

import java.util.ArrayList;
import java.util.List;

import mcib3d.geom.Object3D;
import trackingInterface.ObjectAction;

public class ObjectTree implements ObjectAction {
	
	private Object3D object;
	private ObjectTree parent;
	private List<ObjectTree> children;
	private Integer id;
	
	public ObjectTree(Object3D object) {
		this.object = object;
		this.parent = null;
		this.children = new ArrayList<ObjectTree>();
		this.id = -1;
	}
	
	public Object3D getObject() {
		return object;
	}
	
	public ObjectTree getParent() {
		return parent;
	}
	
	public List<ObjectTree> getChildren() {
		return children;
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

}
