package trackingSPT.objects;

import java.util.List;

public abstract class EventHandlerObjectAction extends ObjectActionSPT {
	
	protected EventSeekerObj eventSeekerObj;

	public abstract List<Event> getEventList();
	
	public abstract void addEvent(Event event);
	
	public abstract void setEventSeekerObjectAction(EventSeekerObj objectAction);
	
	public abstract EventSeekerObj getEventSeekerObj();
	
}
