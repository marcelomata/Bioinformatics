
public class EventSeekerExample extends EventSeeker {

	@Override
	public ObjectAction execute(ObjectAction object) {
		return new EventList();
	}

}
