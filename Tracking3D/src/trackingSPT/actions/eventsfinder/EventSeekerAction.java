package trackingSPT.actions.eventsfinder;

import java.util.ArrayList;
import java.util.List;

import trackingInterface.Action;
import trackingSPT.enums.EventType;
import trackingSPT.objects.events.AssociatedObjectList;
import trackingSPT.objects.events.Event;
import trackingSPT.objects.events.EventMapItem;

public abstract class EventSeekerAction implements Action {

	protected List<Event> events;
	protected EventMapItem eventItem;
	protected AssociatedObjectList associations;
	
	public EventSeekerAction(AssociatedObjectList associations, EventType type) {
		this.events = new ArrayList<Event>();
		this.associations = associations;
		this.eventItem = new EventMapItem(type);
	}
	
	public List<Event> getEventList() {
		return events;
	}
	
	public EventMapItem getEventMapItem() {
		return eventItem;
	}
	
	public AssociatedObjectList getAssociations() {
		return associations;
	}
	
}
