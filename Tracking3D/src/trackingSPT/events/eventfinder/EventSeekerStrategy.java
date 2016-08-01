package trackingSPT.events.eventfinder;

import trackingInterface.Action;
import trackingInterface.Strategy;
import trackingSPT.events.eventfinder.association.AssociationMinDistance;
import trackingSPT.events.eventfinder.mergingsplitting.MergingSimple;
import trackingSPT.events.eventfinder.mergingsplitting.SplittingSimple;
import trackingSPT.objects3D.TrackingContextSPT;

public class EventSeekerStrategy extends Strategy {
	
	private TrackingContextSPT context;

	public EventSeekerStrategy(TrackingContextSPT trackingContext) {
		super();
		this.context = trackingContext;
		build();
	}
	
	public void build() {
		addEventSeekerAction(new ColocalizationAssociation(context));
		addEventSeekerAction(new GoingInAssociation(context));
		addEventSeekerAction(new GoingOutAssociation(context));
		addEventSeekerAction(new AssociationMinDistance(context));
		addEventSeekerAction(new SplittingSimple(context));
		addEventSeekerAction(new MergingSimple(context));
	}
	
	public void run() {
		EventSeekerAction current = (EventSeekerAction) nextAction();
		current.execute();
		
		current = (EventSeekerAction) nextAction();
		current.execute();
		
		current = (EventSeekerAction) nextAction();
		current.execute();
		
		current = (EventSeekerAction) nextAction();
		current.execute();
		
		current = (EventSeekerAction) nextAction();
		current.execute();
		
		current = (EventSeekerAction) nextAction();
		current.execute();
		
		context.calcMeanDistance();
	}
	
	public void addEventSeekerAction(Action action) {
		addAction(action);
	}

}
