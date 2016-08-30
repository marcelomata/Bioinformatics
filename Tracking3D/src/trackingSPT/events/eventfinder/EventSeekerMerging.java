package trackingSPT.events.eventfinder;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import trackingPlugin.Log;
import trackingSPT.events.Event;
import trackingSPT.events.EventMapItem;
import trackingSPT.events.enums.EventCause;
import trackingSPT.events.enums.EventType;
import trackingSPT.math.FunctionCalcObjectRelation;
import trackingSPT.math.FunctionColocalization;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingContextSPT;

/**
 * 
 * Find events of merging, that happen when 2 or more particles in the frame t are completely inside of a particle in the frame t+1.
 * It happens when the segmentation algorithm can not segment the particles properly in the frame t+1.
 * 
 * @author Marcelo da Mata
 *
 */
public class EventSeekerMerging extends EventSeekerAction {
	
	private FunctionCalcObjectRelation function;
	
	public EventSeekerMerging(TrackingContextSPT context) {
		super(context);
		context.addEventType(EventType.MERGING);
		this.function =  new FunctionColocalization();
	}

	@Override
	public void execute() {
		List<Event> events = new ArrayList<Event>();
		List<ObjectTree3D> leftTargetObjects = this.context.getLeftTargetObjects();
		Map<ObjectTree3D, List<ObjectTree3D>> associations = this.context.getAssociationsMap();
		
		//If the number of elements in the frame t is bigger than in the frame t+1
		if(leftTargetObjects.size() > 0) {
			Set<ObjectTree3D> targetsKey = associations.keySet();
			ObjectTree3D obj1;
			ObjectTree3D obj2;
			double distance;
			Event event;
			for (int i = 0; i < leftTargetObjects.size(); i++) {
				obj1 = leftTargetObjects.get(i);
				for (ObjectTree3D key : targetsKey) {
					obj2 = associations.get(key).get(0);
					if(key != obj1) {
						distance = function.calculate(obj1, key);
						distance += function.calculate(obj2, key);
						if(distance == 0) {
							event = new Event(EventCause.COLOCALIZATION, context.getFrameTime());
							event.setObjectSource(key);
							event.setObjectTarget(obj1);
							event.setEventType(EventType.MERGING);
							events.add(event);
							event = new Event(EventCause.COLOCALIZATION, context.getFrameTime());
							event.setObjectSource(key);
							event.setObjectTarget(obj2);
							event.setEventType(EventType.MERGING);
							events.add(event);
							associations.remove(key);
						}
					}
				}
			}
			
		}
		
		EventMapItem item = new EventMapItem(EventType.MERGING);
		item.addEventList(events);
		this.context.addEventItem(item);
		Log.println("Merging events "+events.size());
		
	}
	
}
