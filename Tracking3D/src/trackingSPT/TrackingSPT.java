package trackingSPT;


import java.io.File;

import trackingInterface.TrackingStrategy;
import trackingPlugin.Log;
import trackingSPT.events.enums.TrackingStrategyType;
import trackingSPT.events.eventfinder.EventSeekerTrackingAction;
import trackingSPT.events.eventhandler.EventHandlerTrackingAction;
import trackingSPT.objects3D.TrackingContextSPT;
import trackingSPT.segmentation.SegmentationTrackingAction;

/**
 * 
 * This tool to perform tracking in 2D + t or 3D + t images.
 * This tool was developed to process biomedical applications, but the interface can be used to
 * implement tracking algorithm in another field of interest.
 * 
 * @author Marcelo da Mata
 *
 */
public class TrackingSPT extends TrackingStrategy {
	
	public TrackingSPT(File segmentedDataDir, File rawDataDir, int numMaxFrames) {
		super(segmentedDataDir, rawDataDir, numMaxFrames, TrackingStrategyType.SPT);
	}
	
	@Override
	public void build() {
		buildSTP();
	}

	private void buildSTP() {
		TrackingContextSPT contextSTP = (TrackingContextSPT)context;
		addTrackingAction(new SegmentationTrackingAction(contextSTP));
		addTrackingAction(new EventSeekerTrackingAction(contextSTP));
		addTrackingAction(new EventHandlerTrackingAction(contextSTP));
	}

	@Override
	public void run() {
		TrackingAction current;
		
		while(context.getFrameTime() <= context.getSize()) {
			Log.println("Current Frame -> "+context.getFrameTime());
			context.clear();
			for(int i = 0; i < getNumberOfActions(); i++) {
				current = (TrackingAction) nextAction();
				current.execute();
			}
			Log.println("################################");
			context.nextFrame();
			context.updateObjectsAttributes();
		}
		result = context.getResult();
	}

	@Override
	public void init(File segmentedDataDir, File rawDataDir, int numMaxFrames) {
		this.context = new TrackingContextSPT(segmentedDataDir, rawDataDir, numMaxFrames);
	}

}
