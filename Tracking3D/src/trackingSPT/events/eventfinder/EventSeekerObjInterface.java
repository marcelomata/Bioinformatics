package trackingSPT.events.eventfinder;

import trackingSPT.events.EventHandlerObjectAction;
import trackingSPT.events.eventfinder.association.AssociationSeekerObj;
import trackingSPT.events.eventfinder.mergingmitosis.MistosisMergingObj;
import trackingSPT.objects3D.ObjectActionTracking;


public interface EventSeekerObjInterface extends AssociationSeekerObj, MistosisMergingObj, ObjectActionTracking, EventHandlerObjectAction {
	
	
}
