package trackingSTP.impl;


import trackingInterface.ObjectAction;
import trackingSTP.Handler;

public class HandlerExemple extends Handler {

	@Override
	public ObjectAction execute(ObjectAction object) {
		return new TrackingResult();
	}

}
