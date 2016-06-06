package trackingSPT;

import trackingInterface.ObjectAction;
import trackingInterface.Strategy;
import trackingInterface.TrackingAction;
import trackingSPT.actions.AssociationMinDistance;
import trackingSPT.actions.MergingSimple;
import trackingSPT.actions.SplittingSimple;
import trackingSPT.objects.AssociatedObjectList;
import trackingSPT.objects.EventMap;
import trackingSPT.objects.EventMapItem;
import trackingSPT.objects.TemporalPopulation;

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
