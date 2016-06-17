package trackingSPT.actions.eventsfinder;

import trackingSPT.enums.EventType;
import trackingSPT.objects.TemporalPopulation;
import trackingSPT.objects.events.AssociatedObjectList;

public abstract class AssociationSeeker extends EventSeekerAction {

	protected TemporalPopulation objectAction;
	
	public AssociationSeeker(AssociatedObjectList associations, TemporalPopulation object, EventType type) {
		super(associations, type);
		this.objectAction = object;
	}
	
}
