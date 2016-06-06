package trackingSPT.actions;

import trackingInterface.ObjectAction;
import trackingSPT.EventSeekerAction;
import trackingSPT.objects.AssociatedObjectList;
import trackingSPT.objects.TemporalPopulation;

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
