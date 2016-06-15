package trackingSPT.objects;

import trackingInterface.ObjectAction;
import trackingInterface.ObjectAction4D;

public abstract class TrackingResultObjectAction implements ObjectAction {
	
	protected ObjectAction4D objectAction;

	public abstract void init(ObjectAction objectAction);
	
}
