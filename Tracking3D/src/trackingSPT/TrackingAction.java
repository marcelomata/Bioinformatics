package trackingSPT;

import trackingInterface.Action;
import trackingSPT.objects3D.TrackingContextSPT;

public abstract class TrackingAction implements Action {
	
	protected TrackingContextSPT context;
	
	public TrackingAction(TrackingContextSPT context) {
		this.context = context;
	}

}