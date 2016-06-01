package trackingSTP;


import java.util.List;
import java.util.Map;
import java.util.Set;

import mcib3d.geom.Object3D;
import trackingInterface.Object4D;
import trackingInterface.TrackingAction;
import trackingInterface.TrackingStrategy;
import trackingSTP.actions.AssociationMinDistance;
import trackingSTP.actions.EventSeekerExample;
import trackingSTP.actions.HandlerExemple;
import trackingSTP.objects.AssociationObjectAction;
import trackingSTP.objects.EventHandlerObjectAction;
import trackingSTP.objects.EventSeekerObjectAction;
import trackingSTP.objects.ObjectActionSTP4D;
import trackingSTP.objects.TrackingResultSTP;

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
		AssociationObjectAction object3DToAssociate = null;
		EventSeekerObjectAction associatedObjects = null;
		EventHandlerObjectAction eventList = null;
		TrackingResultSTP result = null;
		ObjectActionSTP4D inObject4D = (ObjectActionSTP4D)inObject;
		inObject4D.nextFrame();
		current = nextAction();
		//To first test, only from the frame 6 to frame 12
		while(inObject4D.getCurrentFrame() < inObject4D.getSize()) {
			if(inObject4D.getCurrentFrame() >= 6 && inObject4D.getCurrentFrame() <= 12) {
				object3DToAssociate = inObject4D.getAssociationLastResult(result);
				current.setObject(object3DToAssociate);
				associatedObjects = (EventSeekerObjectAction) current.execute();
				printAssociationMap(associatedObjects);
				System.out.println("\n\n\n");
				
				current = nextAction();
				current.setObject(associatedObjects);
				eventList = (EventHandlerObjectAction) current.execute();
				
				current = nextAction();
				current.setObject(eventList);
				result = (TrackingResultSTP) current.execute();
			}
			inObject4D.nextFrame();
		}
	}

	private void printAssociationMap(EventSeekerObjectAction associatedObjects) {
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
			if(count > 10) {
				break;
			}
		}
	}

}
