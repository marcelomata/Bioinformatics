package trackingSPT.actions.eventsfinder;

import trackingInterface.Action;
import trackingInterface.Strategy;
import trackingSPT.TrackingAction;
import trackingSPT.objects.TemporalPopulation;
import trackingSPT.objects.TrackingResultObjectAction;
import trackingSPT.objects.events.AssociatedObjectList;
import trackingSPT.objects.events.EventHandlerObjectAction;
import trackingSPT.objects.events.EventMap;

public class EventSeekerStrategyAction extends Strategy implements TrackingAction {
	
	private TemporalPopulation assObjectAction;
	protected EventHandlerObjectAction eventMap;
	private AssociatedObjectList associatedList;

	public EventSeekerStrategyAction(TemporalPopulation object3dToAssociate, EventHandlerObjectAction eventList) {
		super();
		this.associatedList = new AssociatedObjectList();
		this.assObjectAction = object3dToAssociate;
		this.eventMap = eventList;
		build();
	}
	
	public void build() {
		addEventSeekerAction(new AssociationMinDistance(assObjectAction, associatedList));
		addEventSeekerAction(new SplittingSimple(associatedList));
		addEventSeekerAction(new MergingSimple(associatedList));
	}
	
	public void run() {
//		this.associatedList = new AssociatedObjectList();
//		this.eventMap = new EventMap();
		eventMap.setResult(assObjectAction.getResult());
		
		EventSeekerAction current = (EventSeekerAction) nextAction();
		current.execute();
		this.eventMap.addEventItem(current.getEventMapItem());
		associatedList = current.getAssociations();
		this.eventMap.addEventSeekerObj(associatedList);
		
		current = (EventSeekerAction) nextAction();
		current.execute();
		this.eventMap.addEventItem(current.getEventMapItem());
		
		current = (EventSeekerAction) nextAction();
		current.execute();
		this.eventMap.addEventItem(current.getEventMapItem());
	}
	
	public void addEventSeekerAction(Action action) {
		addAction(action);
	}

	@Override
	public void execute() {
		this.run();
	}

	@Override
	public EventHandlerObjectAction getEventHandler() {
		return eventMap;
	}

	@Override
	public TrackingResultObjectAction getResult() {
		return this.eventMap.getResult();
	}

}
