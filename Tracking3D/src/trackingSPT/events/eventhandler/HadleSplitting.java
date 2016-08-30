package trackingSPT.events.eventhandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import trackingPlugin.Log;
import trackingSPT.events.Event;
import trackingSPT.events.enums.EventType;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.SplittedObject;
import trackingSPT.objects3D.TrackingContextSPT;

public class HadleSplitting extends EventHandlerAction {
	
	public HadleSplitting(TrackingContextSPT context) {
		super(context);
	}

	@Override
	public void execute() {
		
		List<Event> splittings = this.context.getEventList(EventType.SPLITTING);
		
		Log.println("Splitting Events -> "+splittings.size());
		
		handleSplitting(splittings);	
	}

	private void handleSplitting(List<Event> splittings) {
		
		//if some splitting happened, so 2 or more events share the same source
		Map<Integer, SplittedObject> sources = new HashMap<Integer, SplittedObject>();
		
		ObjectTree3D source;
		ObjectTree3D target1;
		SplittedObject splittedObj;
		for (Event event : splittings) {
			source = event.getObjectSource();
			if(sources.containsKey(source.getId())) {
				splittedObj = sources.remove(source.getId());
			} else {
				splittedObj = new SplittedObject(source, context.getFrameTime());
				sources.put(source.getId(), splittedObj);
			}
			
			target1 = event.getObjectTarget();
			splittedObj.addTarget(target1);
		}
		
		context.addSplittedObjects(source);
	}
}
