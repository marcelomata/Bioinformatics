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
		
		Object3D source = null;
		Object3D target = null;
		Object3D min = null;
		double minDistance = Double.MAX_VALUE;
		double newDistance = Double.MAX_VALUE;
		
		AssociatedObjectList result = new AssociatedObjectList();
		
		List<Object3D> object3DListSource = object3DT.getObjectsList();
		List<Object3D> object3DListTarget = object3DTPlus1.getObjectsList();
		List<Object3D> leftTargetObject3DList = new ArrayList<Object3D>();
		CostMatrix matrix = new CostMatrix();
		for (Object3D object3d : object3DListTarget) {
			leftTargetObject3DList.add(object3d);
		}
		
		for (int i = 0; i < object3DListSource.size(); i++) {
			source = object3DListSource.get(i);
			min = leftTargetObject3DList.get(0);
			minDistance = source.getCenterAsPoint().distance(min.getCenterAsPoint());
			matrix.addAssociation(source, target);
			matrix.setCost(i, 0, newDistance);
			for (int j = 1; j < leftTargetObject3DList.size(); j++) {
				target = leftTargetObject3DList.get(j);
				newDistance = source.getCenterAsPoint().distance(target.getCenterAsPoint());
				matrix.addAssociation(source, target);
				matrix.setCost(i, j, newDistance);
				if(newDistance < minDistance) {
					minDistance = newDistance;
					min = target;
				}
			}
			result.addAssociation(source, min);
			leftTargetObject3DList.remove(min);
		}
		
		result.addAllLeftTargetObjects(leftTargetObject3DList);
		
//		minDistance = Double.MAX_VALUE;
//		newDistance = Double.MAX_VALUE;
//		for (int i = 0; i < leftObject3DList.size(); i++) {
//			target = leftObject3DList.get(i);
//			source = object3DListSource.get(0);
//			minDistance = source.getCenterAsPoint().distance(target.getCenterAsPoint());
//			min = source;
//			for (int j = 1; j < object3DListSource.size(); j++) {
//				source = object3DListSource.get(j);
//				newDistance = source.getCenterAsPoint().distance(target.getCenterAsPoint());
//				if(newDistance < minDistance) {
//					minDistance = newDistance;
//					min = source;
//				}
//			}
//			result.addAssociation(min, target);
//		}
		
		return result;
	}

}
