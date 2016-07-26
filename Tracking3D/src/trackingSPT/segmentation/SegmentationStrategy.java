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
		SegmentationAction current = (SegmentationAction) nextAction();
		current.execute();
		
		current = (SegmentationAction) nextAction();
		current.execute();
		
		current = (SegmentationAction) nextAction();
		current.execute();

		context.setTemporalPopulation();
	}
	
	public void addEventSegmentationAction(Action action) {
		addAction(action);
	}

}
