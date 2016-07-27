package trackingSPT.events.eventhandler;

import java.util.List;

import trackingInterface.Action;
import trackingInterface.Strategy;
import trackingSPT.events.Event;
import trackingSPT.events.enums.EventType;
import trackingSPT.objects3D.TrackingContextSPT;

public class EventHandlerStrategy extends Strategy {

	private TrackingContextSPT context;

	public EventHandlerStrategy(TrackingContextSPT trackingContext) {
		super();
		this.context = trackingContext;
		build();
	}
	
	public void build() {
		addEventHandlerAction(new HandlerAssociation(context));
		addEventHandlerAction(new HandlerSplitting(context));
		addEventHandlerAction(new HandlerMerging(context));
	}
	
	public void run() {
		List<Event> associations = this.context.getEventList(EventType.ASSOCIATION);
		List<Event> splittings = this.context.getEventList(EventType.SPLITTING);
		List<Event> mergings = this.context.getEventList(EventType.MERGING);
		
		System.out.println("Association Events -> "+associations.size());
		System.out.println("Splitting Events -> "+splittings.size());
		System.out.println("Merging Events -> "+mergings.size());
		
		EventHandlerAction current = (EventHandlerAction) nextAction();
		current.execute();
		
		current = (EventHandlerAction) nextAction();
		current.execute();
		
		current = (EventHandlerAction) nextAction();
		current.execute();
	}
	
	public void addEventHandlerAction(Action action) {
		addAction(action);
	}
	
}
