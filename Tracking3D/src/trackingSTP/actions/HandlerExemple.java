package trackingSTP.actions;


import trackingInterface.ObjectAction;
import trackingSTP.objects.TrackingResult;

public class HandlerExemple extends Handler {

	@Override
	public ObjectAction execute(ObjectAction object) {
		return new TrackingResult();
	}

}
