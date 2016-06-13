package trackingInterface;

import trackingSPT.objects.TrackingResultObjectAction;
import trackingSPT.objects.TrackingResultSPT;

/**
 * 
 * This abstract class defines an strategy to tracking
 *
 */
public abstract class TrackingStrategy extends Strategy {
	
	protected Object4D inObject;
	protected TrackingResultObjectAction result;
	
	public TrackingStrategy(Object4D inObject) {
		super();
		this.inObject = inObject;
		this.result = new TrackingResultSPT();
		result.init(inObject);
		build();
	}
	
	public abstract void build();
	
	public abstract void run();
	
	public void addTrackingAction(TrackingAction action) {
		addAction(action);
	}
	
	public TrackingResultObjectAction getResult() {
		return result;
	}
	
}
