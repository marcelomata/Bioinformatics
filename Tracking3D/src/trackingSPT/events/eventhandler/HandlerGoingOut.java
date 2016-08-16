package trackingSPT.events.eventhandler;


import java.util.List;

import trackingPlugin.Log;
import trackingSPT.events.Event;
import trackingSPT.events.enums.EventType;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingContextSPT;

public class HandlerGoingOut extends EventHandlerAction {
	
	public HandlerGoingOut(TrackingContextSPT context) {
		super(context);
	}

	@Override
	public void execute() {
		List<Event> goingOut = this.context.getEventList(EventType.GOING_OUT);
		
		Log.println("Going Out Events -> "+goingOut.size());
		
		handleGoingIn(goingOut);	
	}

	private void handleGoingIn(List<Event> goingIn) {
		ObjectTree3D obj1;
		for (Event event : goingIn) {
			obj1 = event.getObjectTarget();
			context.finishObjectTracking(obj1);
		}
	}

}