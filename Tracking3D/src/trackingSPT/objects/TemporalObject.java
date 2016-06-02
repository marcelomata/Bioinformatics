package trackingSPT.objects;

import java.util.ArrayList;
import java.util.List;

import mcib3d.geom.Object3D;
import trackingInterface.ObjectAction;

public class TemporalObject implements ObjectAction {
	
	private Object3D object;
	private TemporalObject parent;
	private List<TemporalObject> children;
	
	public TemporalObject(Object3D object) {
		this.object = object;
		this.parent = null;
		this.children = new ArrayList<TemporalObject>();
	}
	
	public Object3D getObject() {
		return object;
	}
	
	public TemporalObject getParent() {
		return parent;
	}
	
	public List<TemporalObject> getChildren() {
		return children;
	}

}
