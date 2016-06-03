package trackingSPT.actions;


import trackingInterface.ObjectAction;
import trackingSPT.objects.TrackingResultSPT;

public class HandlerSimple extends Handler {

	@Override
	public ObjectAction execute() {
		
		return new TrackingResultSPT();
	}

}
