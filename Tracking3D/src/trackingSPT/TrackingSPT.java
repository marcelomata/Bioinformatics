package trackingSPT;


import java.io.File;

import trackingInterface.TrackingStrategy;
import trackingSPT.events.eventfinder.EventSeekerTrackingAction;
import trackingSPT.events.eventhandler.HandlerSimple;
import trackingSPT.objects3D.TrackingContextSPT;
import trackingSPT.segmentation.SegmentationTrackingAction;

public class TrackingSPT extends TrackingStrategy {
	
	private TrackingContextSPT context;
	
	public TrackingSPT(File segmentedDataDir, File rawDataDir) {
		super(segmentedDataDir, rawDataDir);
	}

	@Override
	public void build() {
		addTrackingAction(new SegmentationTrackingAction(context));
		addTrackingAction(new EventSeekerTrackingAction(context));
		addTrackingAction(new HandlerSimple(context));
	}

	@Override
	public void run() {
		TrackingAction current;
		
		while(context.getFrameTime() < context.getSize()) {
			System.out.println("Current Frame -> "+context.getFrameTime());
			context.clear();
			current = (TrackingAction) nextAction();
			current.execute();
			
			current = (TrackingAction) nextAction();
			current.execute();
			
			current = (TrackingAction) nextAction();
			current.execute();
			context.nextFrame();
			System.out.println("################################");
		}
	}

	@Override
	public void init(File segmentedDataDir, File rawDataDir) {
		this.context = new TrackingContextSPT(segmentedDataDir, rawDataDir);
	}

}
