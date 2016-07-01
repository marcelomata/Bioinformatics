package trackingSPT.events;

import java.util.List;

import trackingInterface.ObjectAction;
import trackingSPT.events.enums.EventType;

public interface EventHandlerObjectAction extends ObjectAction {
	
	public abstract void addEventItem(EventMapItem item);
	public abstract void addEventType(EventType item);
	public abstract List<Event> getEventList(EventType type);
	
}