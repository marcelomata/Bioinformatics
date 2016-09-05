package trackingSPT.events.eventhandler;


import java.util.List;

import trackingPlugin.Log;
import trackingSPT.events.Event;
import trackingSPT.events.enums.EventType;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingContextSPT;

public class HandlerMissing extends EventHandlerAction {
	
	public HandlerMissing(TrackingContextSPT context) {
		super(context);
	}

	@Override
	public void execute() {
		
		List<Event> missing = this.context.getEventList(EventType.MISSING);
		
		Log.println("Missing Events -> "+missing.size());
		
		handleMergings(missing);
	}

	private void handleMergings(List<Event> missing) {
		ObjectTree3D obj1;
		ObjectTree3D obj2;
		for (Event event : missing) {
			obj1 = event.getObjectSource();
			obj2 = new ObjectTree3D(obj1.getObject(), this.context.getFrameTime());
			context.addNewObjectId(obj1.getId(), obj2);
		}
	}

}