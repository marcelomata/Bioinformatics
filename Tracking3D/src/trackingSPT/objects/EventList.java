package trackingSPT.objects;

import java.util.ArrayList;
import java.util.List;

import trackingSPT.enums.EventType;

public class EventList extends EventHandlerObjectAction {

	private List<Event> eventList;
	private EventType type;
	
	public EventList() {
		this.eventList = new ArrayList<Event>();
	}
	
	public void addEvent(Event event) {
		this.eventList.add(event);
	}
	
	public List<Event> getEventList() {
		return eventList;
	}

	@Override
	public void setEventSeekerObjectAction(EventSeekerObj objectAction) {
		
	}

	@Override
	public EventSeekerObj getEventSeekerObj() {
		return null;
	}
	
	
}
