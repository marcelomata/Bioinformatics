package trackingSPT.objects;

import trackingInterface.ObjectAction;
import trackingSPT.MotionField;

public abstract class TrackingResultObjectAction implements ObjectAction {

	protected MotionField motionField;
	
	public TrackingResultObjectAction() {
		this.motionField = new MotionField();
	}
	
	public MotionField getMotionField() {
		return motionField;
	}
	
	
}
