package trackingSPT.events.eventhandler;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import trackingPlugin.Log;
import trackingSPT.events.Event;
import trackingSPT.events.enums.EventType;
import trackingSPT.objects3D.MissedObject;
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
		
		handleMissings(missing);
	}

	private void handleMissings(List<Event> missings) {
		Map<Integer, MissedObject> missedObjs = new HashMap<Integer, MissedObject>();
		
		ObjectTree3D obj1;
		ObjectTree3D newObject;
		MissedObject missedObj;
		for (Event event : missings) {
			obj1 = event.getObjectSource();
			missedObj = new MissedObject(obj1, context.getFrameTime());
			missedObjs.put(obj1.getId(), missedObj);
			newObject = new ObjectTree3D(obj1.getObject(), context.getFrameTime());
			context.addNewObjectId(obj1.getId(), newObject);
		}
		context.addMissedObjects(missedObjs);
	}

}