package trackingSPT.events.eventfinder.association;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import trackingSPT.events.eventfinder.mergingsplitting.SplittingMergingObj;
import trackingSPT.math.CostMatrix;
import trackingSPT.objects3D.ObjectTree3D;

public class AssociatedObjectList implements SplittingMergingObj {
	
	private Map<ObjectTree3D, List<ObjectTree3D>> associationsMap;
	private List<ObjectTree3D> leftTargetObjects;
	private List<ObjectTree3D> leftSourceObjects;
	private CostMatrix matrix;
	
	public AssociatedObjectList() {
		this.associationsMap = new HashMap<ObjectTree3D, List<ObjectTree3D>>();
		this.leftTargetObjects = new ArrayList<ObjectTree3D>();
		this.leftSourceObjects = new ArrayList<ObjectTree3D>();
	}
	
	public void addAssociation(ObjectTree3D source, ObjectTree3D target) {
		if(this.associationsMap.containsKey(source)) {
			this.associationsMap.get(source).add(target);
		} else {
			List<ObjectTree3D> newList = new ArrayList<ObjectTree3D>();
			newList.add(target);
			this.associationsMap.put(source, newList);
		}
	}
	
	public Map<ObjectTree3D, List<ObjectTree3D>> getAssociationsMap() {
		return associationsMap;
	}
	
	public void addAllLeftTargetObjects(List<ObjectTree3D> leftTargetObjects) {
		this.leftTargetObjects.addAll(leftTargetObjects);
	}
	
	public void addAllLeftSourceObjects(List<ObjectTree3D> leftSourceObjects) {
		this.leftSourceObjects.addAll(leftSourceObjects);
	}
	
	public void setCostMatrix(CostMatrix matrix) {
		this.matrix = matrix;
	}

	@Override
	public List<ObjectTree3D> getLeftTargetObjects() {
		return this.leftTargetObjects;
	}

	@Override
	public List<ObjectTree3D> getLeftSourceObjects() {
		return this.leftSourceObjects;
	}

	public CostMatrix getCostMatrix() {
		return this.matrix;
	}

	@Override
	public List<ObjectTree3D> getAssociationsMapSources() {
		Set<ObjectTree3D> sourcesSet = associationsMap.keySet();
		List<ObjectTree3D> sourcesList = new ArrayList<ObjectTree3D>();
		for (ObjectTree3D temporalObject : sourcesSet) {
			sourcesList.add(temporalObject);
		}
		return sourcesList;
	}
	
}
