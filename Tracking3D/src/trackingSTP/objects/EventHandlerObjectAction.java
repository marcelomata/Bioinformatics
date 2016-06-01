package trackingSTP.objects;

import java.util.List;

import trackingInterface.ObjectAction;

public abstract class EventHandlerObjectAction implements ObjectAction {
	
	protected EventSeekerObjectAction eventSeekerObj;

	public abstract List<Event> getEventList();
	
	public abstract void addEvent(Event event);
	
	public abstract void setEventSeekerObjectAction(EventSeekerObjectAction objectAction);
	
	public abstract EventSeekerObjectAction getEventSeekerObj();
	
}
