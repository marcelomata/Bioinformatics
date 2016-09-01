package trackingSPT.events.eventhandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mcib3d.geom.Vector3D;
import trackingPlugin.Log;
import trackingSPT.events.Event;
import trackingSPT.events.enums.EventType;
import trackingSPT.objects3D.MergedObject;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingContextSPT;

public class HandlerMerging extends EventHandlerAction {
	
	public HandlerMerging(TrackingContextSPT context) {
		super(context);
	}

	@Override
	public void execute() {
		
		List<Event> mergings = this.context.getEventList(EventType.MERGING);
		
		Log.println("Merging Events -> "+mergings.size());
		
		handleMergings(mergings);	
	}

	private void handleMergings(List<Event> mergings) {
		
		//if some merging happened, so 2 or more events share the same target
		Map<Integer, MergedObject> mergedObjs = new HashMap<Integer, MergedObject>();
		
		ObjectTree3D target;
		ObjectTree3D source1;
		ObjectTree3D newObject;
		MergedObject mergedObj;
		for (Event event : mergings) {
			target = event.getObjectTarget();
			if(mergedObjs.containsKey(target.getId())) {
				mergedObj = mergedObjs.remove(target.getId());
			} else {
				mergedObj = new MergedObject(target, context.getFrameTime());
				mergedObjs.put(target.getId(), mergedObj);
			}
			
			source1 = event.getObjectSource();
			newObject = new ObjectTree3D(source1.getObject(), context.getFrameTime());
			Vector3D v = new Vector3D(newObject.getObject().getCenterAsPoint(), target.getObject().getCenterAsPoint());
			double x = v.getX();
			double y = v.getY();
			double z = v.getZ();
			if(x < y) {
				if(x < z) {
					v.setX(0);
				} else {
					v.setZ(0);
				}
			} else {
				if(y < z) {
					v.setY(0);
				} else {
					v.setZ(0);
				}
			}
			newObject.getObject().translate(v);
			context.addNewObjectId(source1.getId(), newObject);
			newObject.setParent(source1);
			source1.addChild(newObject);
			mergedObj.addSource(source1);
		}
		
		context.addMergedObjects(mergedObjs);
	}
}