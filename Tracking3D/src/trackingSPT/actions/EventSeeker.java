package trackingSPT.actions;

import trackingInterface.ObjectAction;
import trackingInterface.TrackingAction;
import trackingSPT.objects.EventSeekerObjectAction;

public abstract class EventSeeker implements TrackingAction {
	
	protected EventSeekerObjectAction objectAction;
	
	public void setObject(ObjectAction object) {
		this.objectAction = (EventSeekerObjectAction) object;
	}
	
}
