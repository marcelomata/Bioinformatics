package trackingSTP.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mcib3d.geom.Object3D;
import trackingInterface.ObjectAction;

public class AssociatedObjectList implements ObjectAction {
	
	private Map<Object3D, List<Object3D>> associationsMap;
	
	public AssociatedObjectList() {
		this.associationsMap = new HashMap<Object3D, List<Object3D>>();
	}
	
	public void addAssociation(Object3D source, Object3D target) {
		if(this.associationsMap.containsKey(source)) {
			this.associationsMap.get(source).add(target);
		} else {
			List<Object3D> newList = new ArrayList<Object3D>();
			newList.add(target);
			this.associationsMap.put(source, newList);
		}
	}
	
	public Map<Object3D, List<Object3D>> getAssociationsMap() {
		return associationsMap;
	}
	
}
