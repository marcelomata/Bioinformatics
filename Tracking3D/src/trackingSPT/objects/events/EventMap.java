package trackingSPT.objects.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import trackingSPT.enums.EventType;

public class EventMap extends EventHandlerObjectAction {

	private Map<EventType, List<Event>> eventListMap;
	
	public EventMap() {
		this.eventListMap = new HashMap<EventType, List<Event>>();
	}
	
	public void addEventItem(EventMapItem item) {
		List<Event> list = this.eventListMap.get(item.getEventsType());
		if(list == null) {
			list = new ArrayList<Event>();
			this.eventListMap.put(item.getEventsType(), list);
		}
		list.addAll(item.getEventList());
	}
	
	public List<Event> getEventList(EventType type) {
		return eventListMap.get(type);
	}
	
}
