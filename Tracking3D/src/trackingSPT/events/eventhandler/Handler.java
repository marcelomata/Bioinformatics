package trackingSPT.events.eventhandler;

import trackingSPT.TrackingAction;
import trackingSPT.objects3D.TrackingContextSPT;

public abstract class Handler extends TrackingAction {

	public Handler(TrackingContextSPT context) {
		super(context);
	}
	
}
