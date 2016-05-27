package trackingSTP.impl;


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
		
//		for (int i = 0; i < object3DT.getNbObjects(); i++) {
//			
//		}
//		return new AssociatedObjectList();
		
		return null;
	}


}
