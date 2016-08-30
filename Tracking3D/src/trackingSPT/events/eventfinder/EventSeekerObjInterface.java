package trackingSPT.events.eventfinder;

import trackingSPT.events.EventHandlerObjectAction;
import trackingSPT.events.eventfinder.association.AssociationSeekerObj;
import trackingSPT.events.eventfinder.mergingmissingmitosis.MistosisMissingMergingObj;
import trackingSPT.objects3D.ObjectActionTracking;


public interface EventSeekerObjInterface extends AssociationSeekerObj, MistosisMissingMergingObj, ObjectActionTracking, EventHandlerObjectAction {
	
	
}
