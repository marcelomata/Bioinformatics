package trackingSTP.actions;


import trackingInterface.ObjectAction;
import trackingSTP.objects.EventList;

public class EventSeekerExample extends EventSeeker {

	@Override
	public ObjectAction execute(ObjectAction object) {
		return new EventList();
	}

}
