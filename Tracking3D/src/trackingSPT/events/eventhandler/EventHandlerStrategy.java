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
		addEventHandlerAction(new HandlerColocalization(context));
		addEventHandlerAction(new HandlerGoingIn(context));
		addEventHandlerAction(new HandlerGoingOut(context));
		addEventHandlerAction(new HandlerAssociation(context));
		addEventHandlerAction(new HandlerSplitting(context));
		addEventHandlerAction(new HandlerMerging(context));
	}
	
	public void run() {
		List<Event> colocalization = this.context.getEventList(EventType.COLOCALIZATION);
		List<Event> goingIn = this.context.getEventList(EventType.GOING_IN);
		List<Event> goingOut = this.context.getEventList(EventType.GOING_OUT);
		List<Event> associations = this.context.getEventList(EventType.ASSOCIATION);
		List<Event> splittings = this.context.getEventList(EventType.SPLITTING);
		List<Event> mergings = this.context.getEventList(EventType.MERGING);
		
		System.out.println("Colocalization Events -> "+colocalization.size());
		System.out.println("Going In Events -> "+goingIn.size());
		System.out.println("Going Out Events -> "+goingOut.size());
		System.out.println("Association Events -> "+associations.size());
		System.out.println("Splitting Events -> "+splittings.size());
		System.out.println("Merging Events -> "+mergings.size());
		
		EventHandlerAction current = (EventHandlerAction) nextAction();
		current.execute();
		
		current = (EventHandlerAction) nextAction();
		current.execute();
		
		current = (EventHandlerAction) nextAction();
		current.execute();
		
		current = (EventHandlerAction) nextAction();
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
