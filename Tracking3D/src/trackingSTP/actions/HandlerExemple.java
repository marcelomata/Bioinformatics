package trackingSTP.actions;


import java.util.List;
import java.util.Map;

import mcib3d.geom.Object3D;
import trackingInterface.ObjectAction;
import trackingSTP.enums.EventType;
import trackingSTP.objects.Event;
import trackingSTP.objects.EventSeekerObjectAction;
import trackingSTP.objects.TrackingResultSTP;

public class HandlerExemple extends Handler {

	@Override
	public ObjectAction execute() {
		List<Event> events = this.objectAction.getEventList();
		EventSeekerObjectAction eventSeekerObj = this.objectAction.getEventSeekerObj();
		Map<Object3D, List<Object3D>> associationMap = eventSeekerObj.getAssociationsMap();
		Object3D objectEvent = null;
		List<Object3D> associatedList = null;
		
		for (Event event : events) {
			if(event.getEventType() == EventType.SPLITTING) {
				objectEvent = event.getObject();
				associatedList = associationMap.get(objectEvent);
//				findClosest(); TODO
			}
		}
		
		return new TrackingResultSTP();
	}

}
