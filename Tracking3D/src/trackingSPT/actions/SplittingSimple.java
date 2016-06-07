package trackingSPT.actions;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mcib3d.geom.Object3D;
import trackingInterface.ObjectAction;
import trackingSPT.enums.EventCause;
import trackingSPT.enums.EventType;
import trackingSPT.objects.Event;
import trackingSPT.objects.EventMapItem;
import trackingSPT.objects.TemporalObject;

public class SplittingSimple extends SplittingMergingSeeker {

	@Override
	public ObjectAction execute() {
		this.events = new ArrayList<Event>();
		EventMapItem eventItem = new EventMapItem(EventType.SPLITTING);
		List<TemporalObject> leftTargetObjects = this.objectAction.getLeftTargetObjects();
		Map<TemporalObject, List<TemporalObject>> associations = this.objectAction.getAssociationsMap();
		Event event = null;
		//If the number of elements in the frame t+1 is bigger than in the frame t
		if(leftTargetObjects.size() > 0) {
			for (int i = 0; i < leftTargetObjects.size(); i++) {
				event = new Event(EventCause.EXCEEDED);
				event.setObjectTarget(leftTargetObjects.get(i));
				event.setObjectSource(findClosestSource(leftTargetObjects.get(i), associations));
				event.setEventType(EventType.SPLITTING);
				events.add(event);
			}
		}
		
		eventItem.addEventList(events);
		
		return eventItem;
	}
	
	private TemporalObject findClosestSource(TemporalObject temporalObject, Map<TemporalObject, List<TemporalObject>> associations) {
		Set<TemporalObject> targetsKey = associations.keySet();
		TemporalObject minSource = null;
		double minDistance = Double.MAX_VALUE;
		double currentDistance = Double.MAX_VALUE;
		Object3D obj1 = temporalObject.getObject();
		for (TemporalObject key : targetsKey) {
			currentDistance = obj1.getCenterAsPoint().distance(key.getObject().getCenterAsPoint());
			if(currentDistance < minDistance) {
				minDistance = currentDistance;
				minSource = key;
			}
		}
		
		associations.get(minSource).add(temporalObject);
		return minSource;
	}

}
