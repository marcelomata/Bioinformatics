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
import trackingSPT.math.CostMatrix;
import trackingSPT.math.HungarianAlgorithm;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingContextSPT;

public class MissingSimple extends EventSeekerAction {

	public MissingSimple(TrackingContextSPT context) {
		super(context);
		context.addEventType(EventType.MISSING);
	}

	@Override
	public void execute() {
		//when some cell donot have any association and it disappeared on the frame t+1
		//and this cell is also far from the borders
		
		List<ObjectTree3D> leftSourceObjects = this.context.getLeftSourceObjects();
		
		List<Event> events = new ArrayList<Event>();
		Event event;
		
		for (ObjectTree3D obj1 : leftSourceObjects) {
			event = new Event(EventCause.MISSED, context.getFrameTime());
			event.setObjectSource(obj1);
			event.setEventType(EventType.MISSING);
			events.add(event);
		}
		
		EventMapItem item = new EventMapItem(EventType.MISSING);
		item.addEventList(events);
		this.context.addEventItem(item);
		Log.println("Missing events "+events.size());

	}

}
