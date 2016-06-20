package trackingSPT.events.eventhandler;

import java.util.List;

import trackingSPT.enums.EventType;
import trackingSPT.events.Event;
import trackingSPT.objects3D.MissedObject;
import trackingSPT.objects3D.ObjectTree3D;

public interface HandlerObject {
	
	List<Event> getEventList(EventType type);
	
	void addMissed(ObjectTree3D objMissed);
	
	void addNewObjectId(Integer id, ObjectTree3D treeObj);
	
	List<MissedObject> getMisses();

}
