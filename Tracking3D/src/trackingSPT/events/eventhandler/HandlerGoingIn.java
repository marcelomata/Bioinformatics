package trackingSPT.events.eventhandler;


import java.util.List;

import trackingPlugin.Log;
import trackingSPT.events.Event;
import trackingSPT.events.enums.EventType;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingContextSPT;

public class HandlerGoingIn extends EventHandlerAction {
	
	public HandlerGoingIn(TrackingContextSPT context) {
		super(context);
	}

	@Override
	public void execute() {
		
		List<Event> goingIn = this.context.getEventList(EventType.GOING_IN);
		
		Log.println("Going In Events -> "+goingIn.size());
		
		handleGoingIn(goingIn);	
	}

	private void handleGoingIn(List<Event> goingIn) {
		ObjectTree3D obj2;
		for (Event event : goingIn) {
			obj2 = event.getObjectTarget();
			context.addNewObject(obj2);
		}
	}

}