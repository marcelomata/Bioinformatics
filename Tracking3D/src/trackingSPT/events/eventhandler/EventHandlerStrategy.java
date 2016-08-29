package trackingSPT.events.eventhandler;

import java.util.List;

import trackingInterface.Action;
import trackingInterface.Strategy;
import trackingPlugin.Log;
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
		addEventHandlerAction(new HandlerMitosis(context));
		addEventHandlerAction(new HandlerMerging(context));
	}
	
	public void run() {
		List<Event> colocalization = this.context.getEventList(EventType.COLOCALIZATION);
		List<Event> goingIn = this.context.getEventList(EventType.GOING_IN);
		List<Event> goingOut = this.context.getEventList(EventType.GOING_OUT);
		List<Event> associations = this.context.getEventList(EventType.ASSOCIATION);
		List<Event> splittings = this.context.getEventList(EventType.MITOSIS);
		List<Event> mergings = this.context.getEventList(EventType.MERGING);
		
		Log.println("Colocalization Events -> "+colocalization.size());
		Log.println("Going In Events -> "+goingIn.size());
		Log.println("Going Out Events -> "+goingOut.size());
		Log.println("Association Events -> "+associations.size());
		Log.println("Splitting Events -> "+splittings.size());
		Log.println("Merging Events -> "+mergings.size());
		
		EventHandlerAction current;
		for(int i = 0; i < getNumberOfActions(); i++) {
			current = (EventHandlerAction) nextAction();
			current.execute();
		}
//		context.check();
	}
	
	public void addEventHandlerAction(Action action) {
		addAction(action);
	}
	
}
