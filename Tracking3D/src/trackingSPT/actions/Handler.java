package trackingSPT.actions;

import trackingInterface.ObjectAction;
import trackingInterface.TrackingAction;
import trackingSPT.objects.events.EventHandlerObjectAction;

public abstract class Handler implements TrackingAction {

	protected EventHandlerObjectAction objectAction;
	
	public void setObject(ObjectAction object) {
		this.objectAction = (EventHandlerObjectAction) object;
	}
	
}
