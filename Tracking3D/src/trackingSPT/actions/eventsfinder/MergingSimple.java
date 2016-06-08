package trackingSPT.actions.eventsfinder;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mcib3d.geom.Object3D;
import trackingInterface.ObjectAction;
import trackingSPT.enums.EventCause;
import trackingSPT.enums.EventType;
import trackingSPT.objects.TemporalObject;
import trackingSPT.objects.events.Event;
import trackingSPT.objects.events.EventMapItem;

public class MergingSimple extends SplittingMergingSeeker {

	@Override
	public ObjectAction execute() {
		this.events = new ArrayList<Event>();
		EventMapItem eventItem = new EventMapItem(EventType.MERGING);
		List<TemporalObject> leftSourceObjects = this.objectAction.getLeftSourceObjects();
		Map<TemporalObject, List<TemporalObject>> associations = this.objectAction.getAssociationsMap();
		Event event = null;
		//If the number of elements in the frame t+1 is bigger than in the frame t
		if(leftSourceObjects.size() > 0) {
			for (int i = 0; i < leftSourceObjects.size(); i++) {
				event = new Event(EventCause.MISSED);
				event.setObjectSource(leftSourceObjects.get(i));
				event.setObjectTarget(findClosestTarget(leftSourceObjects.get(i), associations));
				event.setEventType(EventType.MERGING);
				events.add(event);
			}
		}
		
		eventItem.addEventList(events);
		
		return eventItem;
	}

	private TemporalObject findClosestTarget(TemporalObject temporalObject, Map<TemporalObject, List<TemporalObject>> associations) {
		Set<TemporalObject> targetsKey = associations.keySet();
		TemporalObject minTarget = null;
		TemporalObject minKey = null;
		double minDistance = Double.MAX_VALUE;
		double currentDistance = Double.MAX_VALUE;
		Object3D obj1 = temporalObject.getObject();
		Object3D obj2 = null;
		for (TemporalObject key : targetsKey) {
			if(key != temporalObject) {
				obj2 = associations.get(key).get(0).getObject();
				currentDistance = obj1.getCenterAsPoint().distance(obj2.getCenterAsPoint());
				if(currentDistance < minDistance) {
					minDistance = currentDistance;
					minTarget = associations.get(key).get(0);
					minKey = key;
				}
			}
		}
		
		associations.put(temporalObject, associations.get(minKey));
		return minTarget;
	}

}
