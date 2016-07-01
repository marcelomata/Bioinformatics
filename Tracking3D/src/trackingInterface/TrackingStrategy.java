package trackingInterface;

import trackingSPT.TrackingAction;
import trackingSPT.objects3D.MovieObjectAction;
import trackingSPT.objects3D.TrackingResultObjectAction;

/**
 * 
 * This abstract class defines an strategy to tracking
 *
 */
public abstract class TrackingStrategy extends Strategy {
	
	protected TrackingResultObjectAction result;
	
	public TrackingStrategy(MovieObjectAction movie) {
		super();
		init(movie);
		build();
	}
	
	public abstract void init(MovieObjectAction movie);
	
	public abstract void build();
	
	public abstract void run();
	
	protected void addTrackingAction(TrackingAction action) {
		addAction(action);
	}
	
	public TrackingResultObjectAction getResult() {
		return result;
	}
	
}
