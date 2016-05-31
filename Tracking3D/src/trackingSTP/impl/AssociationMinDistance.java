package trackingSTP.impl;


import java.util.ArrayList;
import java.util.List;

import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import trackingInterface.ObjectAction;
import trackingSTP.Association;

public class AssociationMinDistance extends Association {

	@Override
	public ObjectAction execute(ObjectAction object) {
		Object3DTracking objectTracking = (Object3DTracking) object;
		Objects3DPopulation object3DT = objectTracking.getObjectT().getObject3D();
		Objects3DPopulation object3DTPlus1 = objectTracking.getObjectTPlus1().getObject3D();
		
		System.out.println(object3DT.getNbObjects() + " and " + object3DTPlus1.getNbObjects());
		
		Object3D source = null;
		Object3D target = null;
		Object3D min = null;
		double minDistance = Double.MAX_VALUE;
		double newDistance = Double.MAX_VALUE;
		
		AssociatedObjectList result = new AssociatedObjectList();
		
		List<Object3D> object3DListSource = object3DT.getObjectsList();
		List<Object3D> object3DListTarget = object3DTPlus1.getObjectsList();
		List<Object3D> leftObject3DList = new ArrayList<Object3D>();
		for (Object3D object3d : object3DListTarget) {
			leftObject3DList.add(object3d);
		}
		
		for (int i = 0; i < object3DListSource.size(); i++) {
			source = object3DListSource.get(i);
			min = leftObject3DList.get(0);
			minDistance = source.getCenterAsPoint().distance(min.getCenterAsPoint());
			for (int j = 1; j < leftObject3DList.size(); j++) {
				target = leftObject3DList.get(j);
				newDistance = source.getCenterAsPoint().distance(target.getCenterAsPoint());
				if(newDistance < minDistance) {
					minDistance = newDistance;
					min = target;
				}
			}
			result.addAssociation(source, min);
			leftObject3DList.remove(min);
//			boolean b = leftObject3DList.remove(min);
//			if(!b) 
//				System.out.println(min.getName()+" - "+b);
		}
		
		minDistance = Double.MAX_VALUE;
		newDistance = Double.MAX_VALUE;
		for (int i = 0; i < leftObject3DList.size(); i++) {
			target = leftObject3DList.get(i);
			source = object3DListSource.get(0);
			minDistance = source.getCenterAsPoint().distance(target.getCenterAsPoint());
			min = source;
			for (int j = 1; j < object3DListSource.size(); j++) {
				source = object3DListSource.get(j);
				newDistance = source.getCenterAsPoint().distance(target.getCenterAsPoint());
				if(newDistance < minDistance) {
					minDistance = newDistance;
					min = source;
				}
			}
			result.addAssociation(min, target);
		}
		
		return result;
	}


}
