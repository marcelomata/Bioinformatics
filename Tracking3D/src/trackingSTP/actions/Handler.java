package trackingSTP.actions;

import trackingInterface.ObjectAction;
import trackingInterface.TrackingAction;
import trackingSTP.objects.EventHandlerObjectAction;

public abstract class Handler implements TrackingAction {

	protected EventHandlerObjectAction objectAction;
	
	public void setObject(ObjectAction object) {
		this.objectAction = (EventHandlerObjectAction) object;
	}
	
}
