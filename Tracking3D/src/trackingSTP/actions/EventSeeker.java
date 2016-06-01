package trackingSTP.actions;

import trackingInterface.ObjectAction;
import trackingInterface.TrackingAction;
import trackingSTP.objects.EventSeekerObjectAction;

public abstract class EventSeeker implements TrackingAction {
	
	protected EventSeekerObjectAction objectAction;
	
	public void setObject(ObjectAction object) {
		this.objectAction = (EventSeekerObjectAction) object;
	}
	
}
