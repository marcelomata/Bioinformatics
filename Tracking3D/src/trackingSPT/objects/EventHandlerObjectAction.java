package trackingSPT.objects;

import java.util.List;

public abstract class EventHandlerObjectAction extends ObjectActionSPT {
	
	protected EventSeekerObjectAction eventSeekerObj;

	public abstract List<Event> getEventList();
	
	public abstract void addEvent(Event event);
	
	public abstract void setEventSeekerObjectAction(EventSeekerObjectAction objectAction);
	
	public abstract EventSeekerObjectAction getEventSeekerObj();
	
}
