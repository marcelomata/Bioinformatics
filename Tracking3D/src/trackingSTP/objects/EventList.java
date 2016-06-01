package trackingSTP.objects;

import java.util.ArrayList;
import java.util.List;

public class EventList extends EventHandlerObjectAction {

	private List<Event> eventList;
	
	public EventList() {
		this.eventList = new ArrayList<Event>();
	}
	
	public void addEvent(Event event) {
		this.eventList.add(event);
	}
	
	public List<Event> getEventList() {
		return eventList;
	}
	
	public void setEventSeekerObjectAction(EventSeekerObjectAction objectAction) {
		this.eventSeekerObj = objectAction;
	}
	
	public EventSeekerObjectAction getEventSeekerObj() {
		return eventSeekerObj;
	}
	
}
