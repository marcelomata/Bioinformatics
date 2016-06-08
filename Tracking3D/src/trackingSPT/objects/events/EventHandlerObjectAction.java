package trackingSPT.objects.events;

import java.util.ArrayList;
import java.util.List;

import trackingSPT.enums.EventType;
import trackingSPT.objects.ObjectActionSPT;

public abstract class EventHandlerObjectAction extends ObjectActionSPT {
	
	protected List<EventSeekerObj> eventSeekerObjList;
	
	public EventHandlerObjectAction() {
		eventSeekerObjList = new ArrayList<EventSeekerObj>();
	}
	
	public abstract void addEventItem(EventMapItem item);
	public abstract List<Event> getEventList(EventType type);
	
	public void addEventSeekerObj(EventSeekerObj eventObj) {
		this.eventSeekerObjList.add(eventObj);
	}
	
}