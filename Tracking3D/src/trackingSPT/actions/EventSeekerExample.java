package trackingSPT.actions;


import java.util.List;

import mcib3d.geom.Object3D;
import trackingInterface.ObjectAction;
import trackingSPT.enums.EventType;
import trackingSPT.enums.ObjectType;
import trackingSPT.objects.Event;
import trackingSPT.objects.EventList;

public class EventSeekerExample extends EventSeeker {

	@Override
	public ObjectAction execute() {
		EventList eventList = new EventList();
		eventList.setEventSeekerObjectAction(this.objectAction);
		List<Object3D> leftTargetObjects = this.objectAction.getLeftTargetObjects();
		List<Object3D> leftSourceObjects = this.objectAction.getLeftSourceObjects();
		Event event = null;
		//If the number of elements in the frame t+1 is bigger than in the frame t
		if(leftTargetObjects.size() > 0) {
			for (int i = 0; i < leftTargetObjects.size(); i++) {
				event = new Event(leftTargetObjects.get(i), ObjectType.EXCEEDED);
				verifyEventType(event);
				eventList.addEvent(event);
			}
		} else if(leftSourceObjects.size() > 0) {
			for (int i = 0; i < leftSourceObjects.size(); i++) {
				event = new Event(leftSourceObjects.get(i), ObjectType.MISSED);
				verifyEventType(event);
				eventList.addEvent(event);
			}
		}
		
		return eventList;
	}

	private void verifyEventType(Event event) {
		//For now, assume that no particle goes in or goes out to scene
		if(event.getObjectType() == ObjectType.EXCEEDED) {
			event.setEventType(EventType.SPLITTING);
		} else if(event.getObjectType() == ObjectType.MISSED) {
			event.setEventType(EventType.MERGING);
		}
	}

}
