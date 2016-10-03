package trackingInterface;

import java.io.File;

import trackingSPT.TrackingAction;
import trackingSPT.events.enums.TrackingStrategyType;
import trackingSPT.objects3D.TrackingResultObjectAction;

/**
 * 
 * This abstract class defines an strategy to tracking
 *
 */
public abstract class TrackingStrategy extends Strategy {
	
	protected TrackingResultObjectAction result;
	protected TrackingContext context;
	protected TrackingStrategyType type;
	
	public TrackingStrategy(File segmentedDataDir, File rawDataDir, int numMaxFrames, TrackingStrategyType type) {
		super();		
		init(segmentedDataDir, rawDataDir, numMaxFrames);
		build();
	}
	
	public abstract void init(File segmentedDataDir, File rawDataDir, int numMaxFrames);
	
	public abstract void build();
	
	public abstract void run();
	
	protected void addTrackingAction(TrackingAction action) {
		addAction(action);
	}
	
	public TrackingResultObjectAction getResult() {
		return result;
	}

	public void generateSegmentationErrors() {
		this.context.generateSegmentationErrorsFile();
	}
	
	public void generateTrackingAnalysis() {
		this.context.generateTrackingAnalysisFiles();
	}
}
