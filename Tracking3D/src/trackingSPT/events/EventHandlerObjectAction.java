package trackingSPT.events;

import java.util.List;

import trackingSPT.events.enums.EventType;

public interface EventHandlerObjectAction {
	
	public abstract void addEventItem(EventMapItem item);
	public abstract void addEventType(EventType item);
	public abstract List<Event> getEventList(EventType type);
	
}