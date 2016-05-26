

import trackingPlugin.Handler;
import trackingPlugin.ObjectAction;
import trackingPlugin.TrackingResult;

public class HandlerExemple extends Handler {

	@Override
	public ObjectAction execute(ObjectAction object) {
		return new TrackingResult();
	}

}
