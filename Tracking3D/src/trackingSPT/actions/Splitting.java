package trackingSPT.actions;

import trackingInterface.EventSeekerAction;
import trackingInterface.ObjectAction;
import trackingSPT.objects.AssociationObjectAction;

public abstract class Splitting implements EventSeekerAction {

	protected AssociationObjectAction objectAction;
	
	public void setObject(ObjectAction object) {
		this.objectAction = (AssociationObjectAction) object;
	}
	
}