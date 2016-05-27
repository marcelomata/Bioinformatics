package trackingSTP.impl;


import trackingInterface.ObjectAction;
import trackingSTP.EventList;
import trackingSTP.EventSeeker;

public class EventSeekerExample extends EventSeeker {

	@Override
	public ObjectAction execute(ObjectAction object) {
		return new EventList();
	}

}
