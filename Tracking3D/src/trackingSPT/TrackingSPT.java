package trackingSPT;


import java.io.File;

import trackingInterface.TrackingStrategy;
import trackingPlugin.Log;
import trackingSPT.events.eventfinder.EventSeekerTrackingAction;
import trackingSPT.events.eventhandler.EventHandlerTrackingAction;
import trackingSPT.objects3D.TrackingContextSPT;
import trackingSPT.segmentation.SegmentationTrackingAction;

public class TrackingSPT extends TrackingStrategy {
	
	private TrackingContextSPT context;
	
	public TrackingSPT(File segmentedDataDir, File rawDataDir, int numMaxFrames) {
		super(segmentedDataDir, rawDataDir, numMaxFrames);
	}
	
//	public TrackingSPT(File imageFile) {
//		super(imageFile);
//	}

	@Override
	public void build() {
		addTrackingAction(new SegmentationTrackingAction(context));
		addTrackingAction(new EventSeekerTrackingAction(context));
		addTrackingAction(new EventHandlerTrackingAction(context));
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
		}
		result = context.getResult();
	}

	@Override
	public void init(File segmentedDataDir, File rawDataDir, int numMaxFrames) {
		this.context = new TrackingContextSPT(segmentedDataDir, rawDataDir, numMaxFrames);
	}

}
