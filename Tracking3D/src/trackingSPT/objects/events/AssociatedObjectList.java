package trackingSPT.objects.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import trackingSPT.math.CostMatrix;
import trackingSPT.objects.ObjectTree;

public class AssociatedObjectList extends SplittingMergingObj {
	
	private Map<ObjectTree, List<ObjectTree>> associationsMap;
	private List<ObjectTree> leftTargetObjects;
	private List<ObjectTree> leftSourceObjects;
	private CostMatrix matrix;
	
	public AssociatedObjectList() {
		this.associationsMap = new HashMap<ObjectTree, List<ObjectTree>>();
		this.leftTargetObjects = new ArrayList<ObjectTree>();
		this.leftSourceObjects = new ArrayList<ObjectTree>();
	}
	
	public void addAssociation(ObjectTree source, ObjectTree target) {
		if(this.associationsMap.containsKey(source)) {
			this.associationsMap.get(source).add(target);
		} else {
			List<ObjectTree> newList = new ArrayList<ObjectTree>();
			newList.add(target);
			this.associationsMap.put(source, newList);
		}
	}
	
	public Map<ObjectTree, List<ObjectTree>> getAssociationsMap() {
		return associationsMap;
	}
	
	public void addAllLeftTargetObjects(List<ObjectTree> leftTargetObjects) {
		this.leftTargetObjects.addAll(leftTargetObjects);
	}
	
	public void addAllLeftSourceObjects(List<ObjectTree> leftSourceObjects) {
		this.leftSourceObjects.addAll(leftSourceObjects);
	}
	
	public void setCostMatrix(CostMatrix matrix) {
		this.matrix = matrix;
	}

	@Override
	public List<ObjectTree> getLeftTargetObjects() {
		return this.leftTargetObjects;
	}

	@Override
	public List<ObjectTree> getLeftSourceObjects() {
		return this.leftSourceObjects;
	}

	public CostMatrix getCostMatrix() {
		return this.matrix;
	}

	@Override
	public List<ObjectTree> getAssociationsMapSources() {
		Set<ObjectTree> sourcesSet = associationsMap.keySet();
		List<ObjectTree> sourcesList = new ArrayList<ObjectTree>();
		for (ObjectTree temporalObject : sourcesSet) {
			sourcesList.add(temporalObject);
		}
		return sourcesList;
	}
	
}
