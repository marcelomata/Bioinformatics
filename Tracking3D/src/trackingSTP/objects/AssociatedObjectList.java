package trackingSTP.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mcib3d.geom.Object3D;
import trackingSTP.math.CostMatrix;

public class AssociatedObjectList extends EventSeekerObjectAction {
	
	private Map<Object3D, List<Object3D>> associationsMap;
	private List<Object3D> leftTargetObjects;
	private List<Object3D> leftSourceObjects;
	private CostMatrix matrix;
	
	public AssociatedObjectList() {
		this.associationsMap = new HashMap<Object3D, List<Object3D>>();
		this.leftTargetObjects = new ArrayList<Object3D>();
		this.leftSourceObjects = new ArrayList<Object3D>();
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
	
	public void addAllLeftTargetObjects(List<Object3D> leftTargetObjects) {
		this.leftTargetObjects.addAll(leftTargetObjects);
	}
	
	public void addAllLeftSourceObjects(List<Object3D> leftSourceObjects) {
		this.leftSourceObjects.addAll(leftSourceObjects);
	}
	
	public void setCostMatrix(CostMatrix matrix) {
		this.matrix = matrix;
	}

	@Override
	public List<Object3D> getLeftTargetObjects() {
		return this.leftTargetObjects;
	}

	@Override
	public List<Object3D> getRightTargetObjects() {
		return this.leftSourceObjects;
	}

	@Override
	public CostMatrix getCostMatrix() {
		return this.matrix;
	}
	
}
