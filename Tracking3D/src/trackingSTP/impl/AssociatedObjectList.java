package trackingSTP.impl;

import java.util.HashMap;
import java.util.Map;

import mcib3d.geom.Object3D;
import trackingInterface.ObjectAction;

public class AssociatedObjectList implements ObjectAction {
	
	private Map<Object3D, Object3D> associationsMap;
	
	public AssociatedObjectList() {
		this.associationsMap = new HashMap<Object3D, Object3D>();
	}
	
	public void addAssociation(Object3D source, Object3D target) {
		this.associationsMap.put(source, target);
	}
	
}
