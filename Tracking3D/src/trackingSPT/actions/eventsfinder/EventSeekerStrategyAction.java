package trackingSPT.actions.eventsfinder;

import trackingInterface.ObjectAction;
import trackingInterface.Strategy;
import trackingInterface.TrackingAction;
import trackingSPT.objects.TemporalPopulation;
import trackingSPT.objects.events.AssociatedObjectList;
import trackingSPT.objects.events.EventMap;
import trackingSPT.objects.events.EventMapItem;

public class EventSeekerStrategyAction extends Strategy implements TrackingAction {
	
	private TemporalPopulation assObjectAction;
	private EventMap eventMap;

	public EventSeekerStrategyAction() {
		super();
	}
	
	public void build() {
		addEventSeekerAction(new AssociationMinDistance());
		addEventSeekerAction(new SplittingSimple());
		addEventSeekerAction(new MergingSimple());
	}
	
	public void run() {
		this.eventMap = new EventMap();
		eventMap.setResult(assObjectAction.getResult());
		
		EventSeekerAction current = (EventSeekerAction) nextAction();
		current.setObject(assObjectAction);
		EventMapItem eventItem = (EventMapItem) current.execute();
		this.eventMap.addEventItem(eventItem);
		AssociatedObjectList associatedList = ((AssociationMinDistance)current).getAssociations();
		this.eventMap.addEventSeekerObj(associatedList);
		
		current = (EventSeekerAction) nextAction();
		current.setObject(associatedList);
		eventItem = (EventMapItem) current.execute();
		this.eventMap.addEventItem(eventItem);
		
		current = (EventSeekerAction) nextAction();
		current.setObject(associatedList);
		eventItem = (EventMapItem) current.execute();
		this.eventMap.addEventItem(eventItem);
	}
	
	public void addEventSeekerAction(EventSeekerAction action) {
		addAction(action);
	}

	@Override
	public void setObject(ObjectAction object) {
		this.assObjectAction = (TemporalPopulation) object;
	}

	@Override
	public ObjectAction execute() {
		this.run();
		return eventMap;
	}
	
}
