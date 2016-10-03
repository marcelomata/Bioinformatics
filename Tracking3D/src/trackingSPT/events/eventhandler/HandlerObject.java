package trackingSPT.events.eventhandler;

import java.util.List;

import trackingSPT.events.Event;
import trackingSPT.events.enums.EventType;
import trackingSPT.objects3D.ObjectTree3D;

public interface HandlerObject {
	
	List<Event> getEventList(EventType type);
	
	void addNewObjectId(Integer id, ObjectTree3D treeObj);
	

}
