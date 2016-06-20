package trackingSPT.events.eventfinder;

import trackingSPT.TrackingAction;
import trackingSPT.objects3D.TrackingContextSPT;

public class EventSeekerTrackingAction extends TrackingAction {

	private EventSeekerStrategy eventSeeker; 
	
	public EventSeekerTrackingAction(TrackingContextSPT context) {
		super(context);
		this.eventSeeker = new EventSeekerStrategy(context);
	}

	@Override
	public void execute() {
		this.eventSeeker.run();
	}

}
