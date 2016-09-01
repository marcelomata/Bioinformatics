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

public class HandlerSplitting extends EventHandlerAction {
	
	public HandlerSplitting(TrackingContextSPT context) {
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
		Map<Integer, SplittedObject> splittedObjs = new HashMap<Integer, SplittedObject>();
		
		ObjectTree3D newObject = null;
		ObjectTree3D source;
		ObjectTree3D target1;
		SplittedObject splittedObj;
		for (Event event : splittings) {
			source = event.getObjectSource();
			if(splittedObjs.containsKey(source.getId())) {
				splittedObj = splittedObjs.remove(source.getId());
				newObject = splittedObj.getTargetObjects().get(0);
			} else {
				splittedObj = new SplittedObject(source, context.getFrameTime());
				splittedObjs.put(source.getId(), splittedObj);
				newObject = event.getObjectTarget();
				//insert only one target object and add the voxels of the others
				//to the inserted object
				source.addChild(newObject);
				newObject.setParent(source);
				context.addNewObjectId(source.getId(), newObject);
			}
			
			target1 = event.getObjectTarget();
			if(newObject != target1) {
				newObject.getObject().getVoxels().addAll(target1.getObject().getVoxels());
			}
			splittedObj.addTarget(target1);
		}
		
		context.addSplittedObjects(splittedObjs);
	}
}
