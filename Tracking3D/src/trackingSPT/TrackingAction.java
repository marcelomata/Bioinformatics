package trackingSPT;

import trackingInterface.Action;
import trackingSPT.objects.TrackingResultObjectAction;
import trackingSPT.objects.events.EventHandlerObjectAction;

public interface TrackingAction extends Action {

	EventHandlerObjectAction getEventHandler();
	
	TrackingResultObjectAction getResult();
	
}
