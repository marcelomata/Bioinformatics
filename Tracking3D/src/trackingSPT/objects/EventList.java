package trackingSPT.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import trackingSPT.enums.EventType;

public class EventList extends EventHandlerObjectAction {

	private Map<EventType, List<Event>> eventListMap;
	
	public EventList() {
		this.eventListMap = new HashMap<EventType, List<Event>>();
	}
	
	public void addAllEvents(List<Event> events, EventType type) {
		this.eventListMap.get(type).addAll(events);
	}
	
	public List<Event> getEventList(EventType type) {
		return eventListMap.get(type);
	}
	
}
