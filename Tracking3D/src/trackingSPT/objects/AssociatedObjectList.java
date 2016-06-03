package trackingSPT.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import trackingSPT.math.CostMatrix;

public class AssociatedObjectList extends SplittingMergingObj {
	
	private Map<TemporalObject, List<TemporalObject>> associationsMap;
	private List<TemporalObject> leftTargetObjects;
	private List<TemporalObject> leftSourceObjects;
	private CostMatrix matrix;
	
	public AssociatedObjectList() {
		this.associationsMap = new HashMap<TemporalObject, List<TemporalObject>>();
		this.leftTargetObjects = new ArrayList<TemporalObject>();
		this.leftSourceObjects = new ArrayList<TemporalObject>();
	}
	
	public void addAssociation(TemporalObject source, TemporalObject target) {
		if(this.associationsMap.containsKey(source)) {
			this.associationsMap.get(source).add(target);
		} else {
			List<TemporalObject> newList = new ArrayList<TemporalObject>();
			newList.add(target);
			this.associationsMap.put(source, newList);
		}
	}
	
	public Map<TemporalObject, List<TemporalObject>> getAssociationsMap() {
		return associationsMap;
	}
	
	public void addAllLeftTargetObjects(List<TemporalObject> leftTargetObjects) {
		this.leftTargetObjects.addAll(leftTargetObjects);
	}
	
	public void addAllLeftSourceObjects(List<TemporalObject> leftSourceObjects) {
		this.leftSourceObjects.addAll(leftSourceObjects);
	}
	
	public void setCostMatrix(CostMatrix matrix) {
		this.matrix = matrix;
	}

	@Override
	public List<TemporalObject> getLeftTargetObjects() {
		return this.leftTargetObjects;
	}

	@Override
	public List<TemporalObject> getLeftSourceObjects() {
		return this.leftSourceObjects;
	}

	public CostMatrix getCostMatrix() {
		return this.matrix;
	}
	
}
