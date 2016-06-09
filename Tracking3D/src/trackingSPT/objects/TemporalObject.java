package trackingSPT.objects;

import java.util.ArrayList;
import java.util.List;

import mcib3d.geom.Object3D;
import trackingInterface.ObjectAction;

public class TemporalObject implements ObjectAction {
	
	private Object3D object;
	private TemporalObject parent;
	private List<TemporalObject> children;
	private Integer id;
	
	public TemporalObject(Object3D object) {
		this.object = object;
		this.parent = null;
		this.children = new ArrayList<TemporalObject>();
		this.id = -1;
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
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}
	
	public boolean isMissed() {
		return object == null;
	}

}
