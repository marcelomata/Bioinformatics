package trackingSPT.events.eventhandler;

import java.util.List;

import trackingPlugin.Log;
import trackingSPT.events.Event;
import trackingSPT.events.enums.EventType;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingContextSPT;

public class HadleSplitting extends EventHandlerAction {
	
	public HadleSplitting(TrackingContextSPT context) {
		super(context);
	}

	@Override
	public void execute() {
		
		List<Event> splittings = this.context.getEventList(EventType.SPLITTING);
		
		Log.println("Splitting Events -> "+splittings.size());
		
		handleSplitting(splittings);	
	}

	private void handleSplitting(List<Event> splittings) {
		
		//TODO implement the hangle splitting
		ObjectTree3D obj1;
		ObjectTree3D obj2;
		double distance = 0;
		//overlap
		for (Event event : splittings) {
			obj1 = event.getObjectSource();
			obj2 = event.getObjectTarget();
			distance = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
			//if the closest is near so a merge should happened
			if(distance < (getMaxAxisBoundBox(obj1.getObject())*context.getMeanDistanceFrame())) {
				context.addMissed(obj1);
			} else {
				context.finishObjectTracking(obj1);
			}
		}
	}
}
