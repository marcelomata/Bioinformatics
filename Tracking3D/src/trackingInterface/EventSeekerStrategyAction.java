package trackingInterface;

import trackingSPT.actions.EventSeekerAssociation;
import trackingSPT.objects.AssociationObjectAction;
import trackingSPT.objects.EventList;

public class EventSeekerStrategyAction extends Strategy implements TrackingAction {
	
	private AssociationObjectAction assObjectAction;
	private EventList eventList;

	public EventSeekerStrategyAction() {
		super();
		this.eventList = new EventList();
	}
	
	public void build() {
		addEventSeekerAction(new EventSeekerAssociation());
	}
	
	public void run() {
		eventList.setResult(assObjectAction.getResult());
		Action current = nextAction();
		current.setObject(assObjectAction);
		current.execute();
	}
	
	public void addEventSeekerAction(EventSeekerAction action) {
		addAction(action);
	}

	@Override
	public void setObject(ObjectAction object) {
		this.assObjectAction = (AssociationObjectAction) object;
	}

	@Override
	public ObjectAction execute() {
		return eventList;
	}
	
}
