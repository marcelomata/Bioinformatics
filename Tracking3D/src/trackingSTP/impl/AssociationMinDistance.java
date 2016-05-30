package trackingSTP.impl;


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
		Object3D minTarget = null;
		double minDistance = Double.MAX_VALUE;
		double newDistance = Double.MAX_VALUE;
		
		AssociatedObjectList result = new AssociatedObjectList();
		
		for (int i = 0; i < object3DT.getNbObjects(); i++) {
			source = object3DT.getObject(i);
			minTarget = object3DTPlus1.getObject(0);
			minDistance = source.getCenterAsPoint().distance(minTarget.getCenterAsPoint());
			for (int j = 1; j < object3DTPlus1.getNbObjects(); j++) {
				target = object3DTPlus1.getObject(j);
				newDistance = source.getCenterAsPoint().distance(target.getCenterAsPoint());
				if(newDistance < minDistance) {
					minDistance = newDistance;
					minTarget = target;
				}
			}
			result.addAssociation(source, minTarget);
		}
		
		return result;
	}


}
