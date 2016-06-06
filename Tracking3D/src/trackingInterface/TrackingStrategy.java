package trackingInterface;

/**
 * 
 * This abstract class defines an strategy to tracking
 *
 */
public abstract class TrackingStrategy extends Strategy {
	
	protected Object4D inObject;
	
	public TrackingStrategy(Object4D inObject) {
		super();
		this.inObject = inObject;
		build();
	}
	
	public abstract void build();
	
	public abstract void run();
	
	public void addTrackingAction(TrackingAction action) {
		addAction(action);
	}
	
}
