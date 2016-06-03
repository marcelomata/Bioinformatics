package trackingSPT.objects;

import trackingSPT.enums.EventType;
import trackingSPT.enums.EventCause;

public class Event {

	private TemporalObject objectSource;
	private TemporalObject objectTarget;
	private EventCause objectType;
	private EventType eventType;
	
	public Event(EventCause objectType) {
		this.objectType = objectType;
		this.objectSource = null;
		this.objectTarget = null;
	}
	
	public TemporalObject getObjectSource() {
		return objectSource;
	}
	
	public void setObjectSource(TemporalObject objectSource) {
		this.objectSource = objectSource;
	}
	
	public void setObjectTarget(TemporalObject objectTarget) {
		this.objectTarget = objectTarget;
	}
	
	public TemporalObject getObjectTarget() {
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
