package trackingSPT;

import java.util.ArrayList;
import java.util.List;

import trackingInterface.Action;
import trackingSPT.objects.Event;

public abstract class EventSeekerAction implements Action {

	protected List<Event> events;
	
	public EventSeekerAction() {
		this.events = new ArrayList<Event>();
	}
	
	public List<Event> getEventList() {
		return events;
	}

}
