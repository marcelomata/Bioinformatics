package trackingSPT.actions.eventsfinder;

import trackingInterface.ObjectAction;
import trackingSPT.objects.events.SplittingMergingObj;

public abstract class SplittingMergingSeeker extends EventSeekerAction {
	
	protected SplittingMergingObj objectAction;
	
	public void setObject(ObjectAction object) {
		this.objectAction = (SplittingMergingObj) object;
	}
	
}
