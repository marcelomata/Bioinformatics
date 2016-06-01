package trackingSTP.actions;

import trackingInterface.ObjectAction;
import trackingInterface.TrackingAction;
import trackingSTP.objects.AssociationObjectAction;

public abstract class Association implements TrackingAction {

	protected AssociationObjectAction objectAction;
	
	public void setObject(ObjectAction object) {
		this.objectAction = (AssociationObjectAction) object;
	}
	
}
