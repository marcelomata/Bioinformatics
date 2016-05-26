package trackingSTP;


import trackingPlugin.EventList;
import trackingPlugin.EventSeeker;
import trackingPlugin.ObjectAction;

public class EventSeekerExample extends EventSeeker {

	@Override
	public ObjectAction execute(ObjectAction object) {
		return new EventList();
	}

}
