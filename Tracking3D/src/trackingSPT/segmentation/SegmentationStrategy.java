package trackingSPT.segmentation;

import trackingInterface.Action;
import trackingInterface.Strategy;
import trackingSPT.objects3D.TrackingContextSPT;

public class SegmentationStrategy extends Strategy {
	
	private TrackingContextSPT context;

	public SegmentationStrategy(TrackingContextSPT trackingContext) {
		super();
		this.context = trackingContext;
		build();
	}
	
	public void build() {
		addEventSegmentationAction(new FilteringAction(context));
		addEventSegmentationAction(new MathMorphologyAction(context));
		addEventSegmentationAction(new Segmentation3D(context));
	}
	
	public void run() {
//		SegmentationAction current;
//		for(int i = 0; i < getNumberOfActions(); i++) {
//			current = (SegmentationAction) nextAction();
//			current.execute();
//		}

		context.getCurrentRawFrame();
		context.setTemporalPopulation();
	}
	
	public void addEventSegmentationAction(Action action) {
		addAction(action);
	}

}
