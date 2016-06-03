package trackingSPT.actions;

import trackingInterface.ObjectAction;
import trackingSPT.EventSeekerAction;
import trackingSPT.objects.SplittingMergingObj;

public abstract class SplittingMergingSeeker extends EventSeekerAction {
	
	protected SplittingMergingObj objectAction;
	
	public void setObject(ObjectAction object) {
		this.objectAction = (SplittingMergingObj) object;
	}
	
}
