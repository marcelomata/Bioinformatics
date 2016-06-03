package trackingSPT;

import trackingInterface.ObjectAction;
import trackingInterface.Strategy;
import trackingInterface.TrackingAction;
import trackingSPT.actions.AssociationMinDistance;
import trackingSPT.actions.MergingSimple;
import trackingSPT.actions.SplittingSimple;
import trackingSPT.enums.EventType;
import trackingSPT.objects.AssociatedObjectList;
import trackingSPT.objects.EventList;
import trackingSPT.objects.TemporalPopulation;

public class EventSeekerStrategyAction extends Strategy implements TrackingAction {
	
	private TemporalPopulation assObjectAction;
	private EventList eventList;

	public EventSeekerStrategyAction() {
		super();
		clear();
	}
	
	private void clear() {
		this.eventList = new EventList();
	}

	public void build() {
		addEventSeekerAction(new AssociationMinDistance());
		addEventSeekerAction(new SplittingSimple());
		addEventSeekerAction(new MergingSimple());
	}
	
	public void run() {
		eventList.setResult(assObjectAction.getResult());
		
		EventSeekerAction current = (EventSeekerAction) nextAction();
		current.setObject(assObjectAction);
		AssociatedObjectList associatedList = (AssociatedObjectList) current.execute();
		this.eventList.addEventSeekerObj(associatedList);
		this.eventList.addAllEvents(current.getEventList(), EventType.ASSOCIATION);
		
		current = (EventSeekerAction) nextAction();
		current.setObject(associatedList);
		current.execute();
		this.eventList.addAllEvents(current.getEventList(), EventType.SPLITTING);
		
		current = (EventSeekerAction) nextAction();
		current.setObject(associatedList);
		current.execute();
		this.eventList.addAllEvents(current.getEventList(), EventType.MERGING);
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
		return null;
	}
	
}
