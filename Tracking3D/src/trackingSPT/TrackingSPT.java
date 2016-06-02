package trackingSPT;


import java.util.List;
import java.util.Map;
import java.util.Set;

import mcib3d.geom.Object3D;
import trackingInterface.Object4D;
import trackingInterface.TrackingAction;
import trackingInterface.TrackingStrategy;
import trackingSPT.actions.AssociationMinDistance;
import trackingSPT.actions.EventSeekerExample;
import trackingSPT.actions.HandlerExemple;
import trackingSPT.objects.AssociationObjectAction;
import trackingSPT.objects.Event;
import trackingSPT.objects.EventHandlerObjectAction;
import trackingSPT.objects.EventSeekerObjectAction;
import trackingSPT.objects.ObjectActionSPT4D;
import trackingSPT.objects.TrackingResultSPT;

public class TrackingSPT extends TrackingStrategy {
	
	public TrackingSPT(Object4D inObject) {
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
		TrackingResultSPT result = new TrackingResultSPT();
		ObjectActionSPT4D inObject4D = (ObjectActionSPT4D)inObject;
		inObject4D.nextFrame();
		
		//To first test, only from the frame 6 to frame 12
		while(inObject4D.getCurrentFrame() < inObject4D.getSize()) {
			if(inObject4D.getCurrentFrame() >= 6 && inObject4D.getCurrentFrame() <= 12) {
				object3DToAssociate = inObject4D.getAssociationLastResult(result);
				current = nextAction();
				current.setObject(object3DToAssociate);
				associatedObjects = (EventSeekerObjectAction) current.execute();
//				printAssociationMap(associatedObjects);
//				System.out.println("\n");
				
				current = nextAction();
				current.setObject(associatedObjects);
				eventList = (EventHandlerObjectAction) current.execute();
				printEventList(eventList);
				
				current = nextAction();
				current.setObject(eventList);
				result = (TrackingResultSPT) current.execute();
			}
			inObject4D.nextFrame();
		}
	}

	private void printEventList(EventHandlerObjectAction eventList) {
		for (Event event : eventList.getEventList()) {
			System.out.println("Object "+event.getObject().getName()+" event type "+event.getEventType());
		}
		System.out.println("\n");
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
