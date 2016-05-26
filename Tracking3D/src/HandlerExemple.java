
public class HandlerExemple extends Handler {

	@Override
	public ObjectAction execute(ObjectAction object) {
		return new TrackingResult();
	}

}
