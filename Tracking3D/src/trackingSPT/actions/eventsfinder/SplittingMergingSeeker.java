package trackingSPT.actions.eventsfinder;

import trackingSPT.enums.EventType;
import trackingSPT.objects.events.AssociatedObjectList;
import trackingSPT.objects.events.SplittingMergingObj;

public abstract class SplittingMergingSeeker extends EventSeekerAction {
	
	protected SplittingMergingObj objectAction;
	
	public SplittingMergingSeeker(AssociatedObjectList associations, EventType type) {
		super(associations, type);
	}

}
