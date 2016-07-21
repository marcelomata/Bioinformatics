package trackingSPT.segmentation;

import trackingInterface.Action;
import trackingInterface.Strategy;
import trackingSPT.events.eventfinder.association.AssociationMinDistance;
import trackingSPT.events.eventfinder.mergingsplitting.MergingSimple;
import trackingSPT.events.eventfinder.mergingsplitting.SplittingSimple;
import trackingSPT.objects3D.TrackingContextSPT;

public class SegmentationStrategy extends Strategy {
	

	public SegmentationStrategy(TrackingContextSPT trackingContext) {
		super();
		build();
	}
	
	public void build() {
//		addEventSeekerAction(new AssociationMinDistance(context));
//		addEventSeekerAction(new SplittingSimple(context));
//		addEventSeekerAction(new MergingSimple(context));
	}
	
	public void run() {
//		EventSeekerAction current = (EventSeekerAction) nextAction();
//		current.execute();
//		
//		current = (EventSeekerAction) nextAction();
//		current.execute();
//		
//		current = (EventSeekerAction) nextAction();
//		current.execute();
	}
	
//	public void addEventSeekerAction(Action action) {
//		addAction(action);
//	}

}
