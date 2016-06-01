package trackingSTP.objects;

import mcib3d.geom.Object3D;
import trackingSTP.enums.EventType;
import trackingSTP.enums.ObjectType;

public class Event {

	private Object3D object;
	private ObjectType objectType;
	private EventType eventType;
	
	public Event(Object3D object, ObjectType objectType) {
		this.object = object;
		this.objectType = objectType;
	}
	
	public Object3D getObject() {
		return object;
	}
	
	public ObjectType getObjectType() {
		return objectType;
	}
	
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	
	public EventType getEventType() {
		return eventType;
	}
}
