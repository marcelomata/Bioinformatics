package trackingSPT.events.eventfinder;


import java.util.ArrayList;
import java.util.List;

import trackingPlugin.Log;
import trackingSPT.events.Event;
import trackingSPT.events.EventMapItem;
import trackingSPT.events.enums.EventCause;
import trackingSPT.events.enums.EventType;
import trackingSPT.math.FunctionCalcObjectRelation;
import trackingSPT.math.FunctionSplittingMergingColocalization;
import trackingSPT.objects3D.MergedObject;
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
		this.function =  new FunctionSplittingMergingColocalization();
	}

	@Override
	public void execute() {
		List<Event> events = new ArrayList<Event>();
		List<ObjectTree3D> leftSourceObjects = this.context.getLeftSourceObjects();
		List<ObjectTree3D> leftTargetObjects = this.context.getLeftTargetObjects();
		
		//If the number of elements in the frame t is bigger than in the frame t+1
		if(leftSourceObjects.size() > 0) {
			ObjectTree3D source;
			ObjectTree3D target;
			double distance;
			MergedObject mergedObject;
			Event event;
			List<ObjectTree3D> sources ;
			for (int j = 0; j < leftTargetObjects.size(); j++) {
				target = leftTargetObjects.get(j);
				mergedObject = new MergedObject(target, context.getFrameTime());
				for (int i = 0; i < leftSourceObjects.size(); i++) {
					source = leftSourceObjects.get(i);
					distance = function.calculate(target, source);
					if(distance <= 0.99) {
						mergedObject.addSource(source);
					}
				}
				sources = mergedObject.getSourceObjects();
				//If the object was merged from 2 or more objects
				//So create the merging events
				if(sources.size() > 1) {
					for (ObjectTree3D mergedSource : sources) {
						event = new Event(EventCause.COLOCALIZATION, context.getFrameTime());
						event.setObjectSource(mergedSource);
						event.setObjectTarget(target);
						event.setEventType(EventType.MERGING);
						events.add(event);
						leftSourceObjects.remove(mergedSource);
					}
					leftTargetObjects.remove(target);
					j--;
				}
			}
			
		}
		
		EventMapItem item = new EventMapItem(EventType.MERGING);
		item.addEventList(events);
		this.context.addEventItem(item);
		Log.println("Merging events "+events.size());
		
	}
	
}
