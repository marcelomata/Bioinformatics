package trackingSPT.actions;

import trackingInterface.ObjectAction;
import trackingSPT.EventSeekerAction;
import trackingSPT.objects.TemporalPopulation;

public abstract class AssociationSeeker extends EventSeekerAction {

	protected TemporalPopulation objectAction;
	
	@Override
	public void setObject(ObjectAction object) {
		this.objectAction = (TemporalPopulation) object;
	}

}
