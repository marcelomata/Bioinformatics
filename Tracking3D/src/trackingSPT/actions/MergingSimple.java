package trackingSPT.actions;


import java.util.List;

import trackingInterface.ObjectAction;
import trackingSPT.enums.EventCause;
import trackingSPT.enums.EventType;
import trackingSPT.objects.Event;
import trackingSPT.objects.TemporalObject;

public class MergingSimple extends SplittingMergingSeeker {

	@Override
	public ObjectAction execute() {
		List<TemporalObject> leftSourceObjects = this.objectAction.getLeftSourceObjects();
		Event event = null;
		//If the number of elements in the frame t+1 is bigger than in the frame t
		if(leftSourceObjects.size() > 0) {
			for (int i = 0; i < leftSourceObjects.size(); i++) {
				event = new Event(EventCause.MISSED);
				event.setObjectSource(leftSourceObjects.get(i));
				event.setEventType(EventType.MERGING);
				events.add(event);
			}
		}
		
		return null;
	}

}
