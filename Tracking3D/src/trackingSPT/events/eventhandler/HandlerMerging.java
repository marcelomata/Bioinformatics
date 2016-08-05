package trackingSPT.events.eventhandler;


import java.util.List;

import trackingSPT.events.Event;
import trackingSPT.events.enums.EventType;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingContextSPT;

public class HandlerMerging extends EventHandlerAction {
	
	public HandlerMerging(TrackingContextSPT context) {
		super(context);
	}

	@Override
	public void execute() {
		
		List<Event> mergings = this.context.getEventList(EventType.MERGING);
		
		System.out.println("Merging Events -> "+mergings.size());
		
		handleMergings(mergings);	
	}

	private void handleMergings(List<Event> mergings) {
		ObjectTree3D obj1;
		ObjectTree3D obj2;
		double distance;
		//overlap
		for (Event event : mergings) {
			obj1 = event.getObjectSource();
			obj2 = event.getObjectTarget();
			distance = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
			//if the closest is near so a merge sould happened
			if(distance < (getMaxAxisBoundBox(obj1.getObject())*context.getMeanDistanceFrame())) {
				context.addMissed(obj1);
			} else {
				context.finishObjectTracking(obj1);
			}
//			context.finishObjectTracking(obj1);
		}
	}

}