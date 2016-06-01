package trackingSTP.actions;


import java.util.ArrayList;
import java.util.List;

import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import trackingInterface.ObjectAction;
import trackingSTP.math.CostMatrix;
import trackingSTP.objects.AssociatedObjectList;

public class AssociationMinDistance extends Association {

	@Override
	public ObjectAction execute() {
		Objects3DPopulation object3DT = objectAction.getObjectT().getObject3D();
		Objects3DPopulation object3DTPlus1 = objectAction.getObjectTPlus1().getObject3D();
		
		System.out.println(object3DT.getNbObjects() + " and " + object3DTPlus1.getNbObjects());
		
		List<Object3D> object3DListSource = object3DT.getObjectsList();
		List<Object3D> object3DListTarget = object3DTPlus1.getObjectsList();
		List<Object3D> leftTargetObject3DList = new ArrayList<Object3D>();
		List<Object3D> leftSourceObject3DList = new ArrayList<Object3D>();
		CostMatrix matrix = new CostMatrix(object3DListSource.size(), object3DListTarget.size());
		for (Object3D object3d : object3DListTarget) {
			leftTargetObject3DList.add(object3d);
		}
		for (Object3D object3d : object3DListSource) {
			leftSourceObject3DList.add(object3d);
		}
		
		AssociatedObjectList result = findShortestDistance(leftSourceObject3DList, leftTargetObject3DList, matrix);
		result.setCostMatrix(matrix);
		result.addAllLeftTargetObjects(leftTargetObject3DList);
		result.addAllLeftSourceObjects(leftSourceObject3DList);
		
		return result;
	}
	
	private AssociatedObjectList findShortestDistance(List<Object3D> source, List<Object3D> target, CostMatrix matrix) {
		List<Object3D> list1 = target;
		List<Object3D> list2 = source;
		boolean sourceFirst = false;
		if(source.size() < target.size()) {
			list1 = source;
			list2 = target;
			sourceFirst = true;
			System.out.println("true");
		}
		
		AssociatedObjectList result = new AssociatedObjectList();
		
		Object3D obj1 = null;
		Object3D obj2 = null;
		Object3D min = null;
		double minDistance = Double.MAX_VALUE;
		double newDistance = Double.MAX_VALUE;
		
		for (int i = 0; i < list1.size(); i++) {
			obj1 = list1.get(i);
			for (int j = 0; j < list2.size(); j++) {
				obj2 = list2.get(j);
				newDistance = obj1.getCenterAsPoint().distance(obj2.getCenterAsPoint());
				if(sourceFirst) {
					matrix.addAssociation(obj1, obj2);
					matrix.setCost(i, j, newDistance);
				} else {
					matrix.addAssociation(obj2, obj1);
					matrix.setCost(j, i, newDistance);
				}
				if(newDistance < minDistance) {
					minDistance = newDistance;
					min = obj2;
				}
			}
			list2.remove(min);
			if(sourceFirst) {
				result.addAssociation(obj1, min);
			} else {
				result.addAssociation(min, obj1);
			}
			min = null;
			minDistance = Double.MAX_VALUE;
		}
		list1.clear();
		
		return result;
	}

}
