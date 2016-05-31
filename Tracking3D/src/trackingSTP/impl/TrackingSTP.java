package trackingSTP.impl;


import java.util.List;
import java.util.Map;
import java.util.Set;

import mcib3d.geom.Object3D;
import trackingInterface.Object4D;
import trackingInterface.TrackingAction;
import trackingInterface.TrackingStrategy;

public class TrackingSTP extends TrackingStrategy {
	
	public TrackingSTP(Object4D inObject) {
		super(inObject);
	}

	@Override
	public void build() {
		addAction(new AssociationMinDistance());
		addAction(new EventSeekerExample());
		addAction(new HandlerExemple());
	}

	@Override
	public void run() {
		TrackingAction current = null;
		Object3DTracking object3DToAssociate = null;
		AssociatedObjectList associatedObjects = null;
		ObjectAction4D inObject4D = (ObjectAction4D)inObject;
		inObject4D.nextFrame();
		current = nextAction();
		//To first test, only from the frame 6 to frame 12
		while(inObject4D.getCurrentFrame() < inObject4D.getSize()) {
			if(inObject4D.getCurrentFrame() >= 6 && inObject4D.getCurrentFrame() <= 12) {
				object3DToAssociate = new Object3DTracking(inObject4D.getLastFrame(), inObject4D.getFrame());
				associatedObjects = (AssociatedObjectList) current.execute(object3DToAssociate);
	//			current = nextAction();
				
				printAssociationMap(associatedObjects);
			}
			
			inObject4D.nextFrame();
			System.out.println("\n\n\n");
		}
	}

	private void printAssociationMap(AssociatedObjectList associatedObjects) {
		Map<Object3D, List<Object3D>> associationsMap = associatedObjects.getAssociationsMap();
		Set<Object3D> keys = associationsMap.keySet();
		int count = 1;
		for (Object3D object3d : keys) {
			System.out.print(count + " Object "+object3d.getName()+" associated to ");
			List<Object3D> list = associationsMap.get(object3d);
			for (Object3D object3d2 : list) {
				System.out.print(object3d2.getName()+" - ");
			}
			System.out.println();
			count++;
		}
	}

}
