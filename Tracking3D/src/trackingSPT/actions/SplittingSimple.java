package trackingSPT.actions;


import java.util.List;

import trackingInterface.ObjectAction;
import trackingSPT.enums.EventCause;
import trackingSPT.enums.EventType;
import trackingSPT.objects.Event;
import trackingSPT.objects.TemporalObject;

public class SplittingSimple extends SplittingMergingSeeker {

	@Override
	public ObjectAction execute() {
		List<TemporalObject> leftTargetObjects = this.objectAction.getLeftTargetObjects();
		Event event = null;
		//If the number of elements in the frame t+1 is bigger than in the frame t
		if(leftTargetObjects.size() > 0) {
			for (int i = 0; i < leftTargetObjects.size(); i++) {
				event = new Event(EventCause.EXCEEDED);
				event.setObjectTarget(leftTargetObjects.get(i));
				event.setEventType(EventType.SPLITTING);
				events.add(event);
			}
		}
		
		return null;
	}

}
