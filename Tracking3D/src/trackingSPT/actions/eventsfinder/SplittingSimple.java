package trackingSPT.actions.eventsfinder;


import java.util.ArrayList;
import java.util.List;

import mcib3d.geom.Object3D;
import trackingInterface.ObjectAction;
import trackingSPT.enums.EventCause;
import trackingSPT.enums.EventType;
import trackingSPT.objects.TemporalObject;
import trackingSPT.objects.events.Event;
import trackingSPT.objects.events.EventMapItem;

public class SplittingSimple extends SplittingMergingSeeker {

	@Override
	public ObjectAction execute() {
		this.events = new ArrayList<Event>();
		EventMapItem eventItem = new EventMapItem(EventType.SPLITTING);
		List<TemporalObject> leftTargetObjects = this.objectAction.getLeftTargetObjects();
		List<TemporalObject> associationsSources = this.objectAction.getAssociationsMapSources();
		Event event = null;
		//If the number of elements in the frame t+1 is bigger than in the frame t
		if(leftTargetObjects.size() > 0) {
			for (int i = 0; i < leftTargetObjects.size(); i++) {
				event = new Event(EventCause.EXCEEDED);
				event.setObjectTarget(leftTargetObjects.get(i));
				event.setObjectSource(findClosestSource(leftTargetObjects.get(i), associationsSources));
				event.setEventType(EventType.SPLITTING);
				events.add(event);
			}
		}
		
		eventItem.addEventList(events);
		
		return eventItem;
	}
	
	private TemporalObject findClosestSource(TemporalObject temporalObject, List<TemporalObject> associationsSources) {
		TemporalObject minSource = null;
		double minDistance = Double.MAX_VALUE;
		double currentDistance = Double.MAX_VALUE;
		Object3D obj1 = temporalObject.getObject();
		TemporalObject key = null;
		for (int i = 0; i < associationsSources.size(); i++) {
			key = associationsSources.get(i);
			currentDistance = obj1.getCenterAsPoint().distance(key.getObject().getCenterAsPoint());
			if(currentDistance < minDistance) {
				minDistance = currentDistance;
				minSource = key;
			}
		}
		
		this.objectAction.getAssociationsMap().get(minSource).add(temporalObject);
		associationsSources.remove(minSource);
		return minSource;
	}

}
