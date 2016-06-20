package trackingSPT.events;

import trackingSPT.events.enums.EventCause;
import trackingSPT.events.enums.EventType;
import trackingSPT.objects3D.ObjectTree3D;

public class Event {

	private ObjectTree3D objectSource;
	private ObjectTree3D objectTarget;
	private EventCause objectType;
	private EventType eventType;
	
	public Event(EventCause objectType) {
		this.objectType = objectType;
		this.objectSource = null;
		this.objectTarget = null;
	}
	
	public ObjectTree3D getObjectSource() {
		return objectSource;
	}
	
	public void setObjectSource(ObjectTree3D objectSource) {
		this.objectSource = objectSource;
	}
	
	public void setObjectTarget(ObjectTree3D objectTarget) {
		this.objectTarget = objectTarget;
	}
	
	public ObjectTree3D getObjectTarget() {
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
