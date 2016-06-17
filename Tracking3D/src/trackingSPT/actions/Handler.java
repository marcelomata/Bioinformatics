package trackingSPT.actions;

import trackingSPT.TrackingAction;
import trackingSPT.objects.events.EventHandlerObjectAction;

public abstract class Handler implements TrackingAction {

	protected EventHandlerObjectAction objectAction;
	
	public Handler(EventHandlerObjectAction objectAction) {
		this.objectAction = objectAction;
	}
	
}
