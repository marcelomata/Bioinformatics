package trackingSPT.events.eventfinder;

import trackingInterface.Action;
import trackingSPT.objects3D.TrackingContextSPT;

public abstract class EventSeekerAction implements Action {

	protected TrackingContextSPT context;
	
	public EventSeekerAction(TrackingContextSPT context) {
		this.context = context;
	}
	
}
