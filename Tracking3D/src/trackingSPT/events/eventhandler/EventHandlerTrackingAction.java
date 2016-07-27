package trackingSPT.events.eventhandler;

import trackingSPT.TrackingAction;
import trackingSPT.objects3D.TrackingContextSPT;

public class EventHandlerTrackingAction extends TrackingAction {

	private EventHandlerStrategy eventHandle; 
	
	public EventHandlerTrackingAction(TrackingContextSPT context) {
		super(context);
		this.eventHandle = new EventHandlerStrategy(context);
	}

	@Override
	public void execute() {
		if(context.getCurrentFrame() > 0) {
			this.eventHandle.run();
		}
	}

}
