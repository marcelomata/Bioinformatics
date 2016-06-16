package trackingSPT.objects.events;

import trackingSPT.enums.EventCause;
import trackingSPT.enums.EventType;
import trackingSPT.objects.ObjectTree;

public class Event {

	private ObjectTree objectSource;
	private ObjectTree objectTarget;
	private EventCause objectType;
	private EventType eventType;
	
	public Event(EventCause objectType) {
		this.objectType = objectType;
		this.objectSource = null;
		this.objectTarget = null;
	}
	
	public ObjectTree getObjectSource() {
		return objectSource;
	}
	
	public void setObjectSource(ObjectTree objectSource) {
		this.objectSource = objectSource;
	}
	
	public void setObjectTarget(ObjectTree objectTarget) {
		this.objectTarget = objectTarget;
	}
	
	public ObjectTree getObjectTarget() {
		return objectTarget;
	}
	
	public EventCause getObjectType() {
		return objectType;
	}
	
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	
	public EventType getEventType() {
		return eventType;
	}
}
