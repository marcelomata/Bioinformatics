package trackingSPT.segmentation;

import trackingSPT.TrackingAction;
import trackingSPT.objects3D.TrackingContextSPT;

public class SegmentationTrackingAction extends TrackingAction  {

	private SegmentationStrategy segmentationStrategy; 
	
	public SegmentationTrackingAction(TrackingContextSPT context) {
		super(context);
		this.segmentationStrategy = new SegmentationStrategy(context);
	}

	@Override
	public void execute() {
		this.segmentationStrategy.run();
	}

}
