package trackingSPT.events.eventfinder;

import trackingInterface.Action;
import trackingInterface.Strategy;
import trackingSPT.events.eventfinder.association.AssociationMinDistance;
import trackingSPT.objects3D.TrackingContextSPT;

public class EventSeekerStrategy extends Strategy {
	
	private TrackingContextSPT context;

	public EventSeekerStrategy(TrackingContextSPT trackingContext) {
		super();
		this.context = trackingContext;
		build();
	}
	
	public void build() {
		addEventSeekerAction(new EventSeekerSplitting(context));
		addEventSeekerAction(new EventSeekerMerging(context));
		addEventSeekerAction(new ColocalizationAssociation(context));
		addEventSeekerAction(new GoingInAssociation(context));
		addEventSeekerAction(new GoingOutAssociation(context));
		addEventSeekerAction(new AssociationMinDistance(context));
		addEventSeekerAction(new MistosisSimple(context));
		addEventSeekerAction(new MissingSimple(context));
	}
	
	public void run() {
		EventSeekerAction current;
		
		for(int i = 0; i < getNumberOfActions(); i++) {
			current = (EventSeekerAction) nextAction();
			current.execute();
		}
	}
	
	public void addEventSeekerAction(Action action) {
		addAction(action);
	}

}
