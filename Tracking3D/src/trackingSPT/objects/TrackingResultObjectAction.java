package trackingSPT.objects;

import trackingInterface.ObjectAction;

public abstract class TrackingResultObjectAction implements ObjectAction {
	
	protected MovieObjectAction objectAction;
	
	public TrackingResultObjectAction(ObjectActionSPT4D inObject) {
		this.objectAction = inObject;
	}

	public abstract void init();
	
}
