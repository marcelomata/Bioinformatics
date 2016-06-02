package trackingSPT.objects;

import trackingInterface.ObjectAction;

public abstract class ObjectActionSPT implements ObjectAction {
	
	protected TrackingResultObjectAction result;
	
	public TrackingResultObjectAction getResult() {
		return result;
	}
	
	public void setResult(TrackingResultObjectAction result) {
		this.result = result;
	}

}
