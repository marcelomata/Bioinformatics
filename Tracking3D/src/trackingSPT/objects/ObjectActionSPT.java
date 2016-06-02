package trackingSPT.objects;

import trackingInterface.ObjectAction;

public abstract class ObjectActionSPT implements ObjectAction {
	
	protected TrackingResultSPT result;
	
	public TrackingResultSPT getResult() {
		return result;
	}
	
	public void setResult(TrackingResultSPT result) {
		this.result = result;
	}

}
