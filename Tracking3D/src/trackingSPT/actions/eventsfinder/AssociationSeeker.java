package trackingSPT.actions.eventsfinder;

import trackingInterface.ObjectAction;
import trackingSPT.objects.TemporalPopulation;
import trackingSPT.objects.events.AssociatedObjectList;

public abstract class AssociationSeeker extends EventSeekerAction {

	protected TemporalPopulation objectAction;
	protected AssociatedObjectList associations;
	
	@Override
	public void setObject(ObjectAction object) {
		this.objectAction = (TemporalPopulation) object;
	}
	
	public AssociatedObjectList getAssociations() {
		return associations;
	}

}
