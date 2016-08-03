package trackingInterface;

import java.io.File;

import trackingSPT.TrackingAction;
import trackingSPT.objects3D.TrackingResultObjectAction;

/**
 * 
 * This abstract class defines an strategy to tracking
 *
 */
public abstract class TrackingStrategy extends Strategy {
	
	protected TrackingResultObjectAction result;
	
	public TrackingStrategy(File segmentedDataDir, File rawDataDir) {
		super();
		init(segmentedDataDir, rawDataDir);
		build();
	}
	
//	public TrackingStrategy(File imageFile) {
//		super();
//		init(imageFile);
//		build();
//	}
	
	public abstract void init(File segmentedDataDir, File rawDataDir);
	
//	public abstract void init(File imageFile);
	
	public abstract void build();
	
	public abstract void run();
	
	protected void addTrackingAction(TrackingAction action) {
		addAction(action);
	}
	
	public TrackingResultObjectAction getResult() {
		return result;
	}
	
}
