package trackingSPT.events;

import java.util.ArrayList;
import java.util.List;

import trackingSPT.events.enums.EventType;

public class EventMapItem {

	private List<Event> eventList;
	private EventType type;
	
	public EventMapItem(EventType type) {
		this.eventList = new ArrayList<Event>();
		this.type = type;
	}
	
	public void addEventList(List<Event> eventList) {
		this.eventList.addAll(eventList);
	}
	
	public EventType getEventsType() {
		return this.type;
	}
	
	public List<Event> getEventList() {
		return eventList;
	}
	
}
