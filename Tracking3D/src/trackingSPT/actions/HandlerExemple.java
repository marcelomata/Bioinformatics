package trackingSPT.actions;


import java.util.List;
import java.util.Map;

import mcib3d.geom.Object3D;
import trackingInterface.ObjectAction;
import trackingSPT.enums.EventType;
import trackingSPT.objects.Event;
import trackingSPT.objects.EventSeekerObj;
import trackingSPT.objects.TrackingResultSPT;

public class HandlerExemple extends Handler {

	@Override
	public ObjectAction execute() {
		List<Event> events = this.objectAction.getEventList();
		EventSeekerObj eventSeekerObj = this.objectAction.getEventSeekerObj();
//		Map<Object3D, List<Object3D>> associationMap = eventSeekerObj.getAssociationsMap();
		Map<Object3D, List<Object3D>> associationMap = null;
		Object3D objectEvent = null;
		List<Object3D> associatedList = null;
		
		for (Event event : events) {
			if(event.getEventType() == EventType.SPLITTING) {
				objectEvent = event.getObject();
				associatedList = associationMap.get(objectEvent);
//				findClosest(); TODO
			}
		}
		
		return new TrackingResultSPT();
	}

}
