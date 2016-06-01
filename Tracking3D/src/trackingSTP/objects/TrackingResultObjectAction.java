package trackingSTP.objects;

import trackingInterface.ObjectAction;
import trackingSTP.MotionField;

public abstract class TrackingResultObjectAction implements ObjectAction {

	protected MotionField motionField;
	
	public TrackingResultObjectAction() {
		this.motionField = new MotionField();
	}
	
	
}
