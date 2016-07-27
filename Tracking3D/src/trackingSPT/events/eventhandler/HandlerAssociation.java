package trackingSPT.events.eventhandler;


import java.util.List;

import trackingSPT.events.Event;
import trackingSPT.events.enums.EventType;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingContextSPT;

public class HandlerAssociation extends EventHandlerAction {
	
	public HandlerAssociation(TrackingContextSPT context) {
		super(context);
	}

	@Override
	public void execute() {
		List<Event> associations = this.context.getEventList(EventType.ASSOCIATION);
		
		System.out.println("Mean of shorter distances -> " + context.getMeanDistanceFrame());
		
		handleAssociations(associations);
	}

	private void handleAssociations(List<Event> associations) {
		Event temp = null;
		double distance;
		for (int i = 0; i < associations.size(); i++) {
			temp = associations.get(i);
			ObjectTree3D obj1 = temp.getObjectSource();
			ObjectTree3D obj2 = temp.getObjectTarget();
			distance = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
			double comp = getDistCenterMax(obj1.getObject());
			comp = Double.isNaN(comp) ? context.getMeanDistanceFrame() : comp; 
			if(distance < (comp*context.getMeanDistanceFrame())) {
				context.addNewObjectId(obj1.getId(), obj2);
				obj2.setParent(obj1);
				obj1.addChild(obj2);
			} else {
				context.finishObjectTracking(obj1);
				context.addNewObject(obj2);
			}
		}
	}

}