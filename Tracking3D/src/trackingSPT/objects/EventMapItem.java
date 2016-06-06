package trackingSPT.objects;

import java.util.ArrayList;
import java.util.List;

import trackingInterface.ObjectAction;
import trackingSPT.enums.EventType;

public class EventMapItem implements ObjectAction {

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
