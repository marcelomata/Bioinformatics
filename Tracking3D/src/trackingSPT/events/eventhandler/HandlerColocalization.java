package trackingSPT.events.eventhandler;


import java.util.List;

import trackingSPT.events.Event;
import trackingSPT.events.enums.EventType;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingContextSPT;

public class HandlerColocalization extends EventHandlerAction {
	
	public HandlerColocalization(TrackingContextSPT context) {
		super(context);
	}

	@Override
	public void execute() {
		
		List<Event> colocalizations = this.context.getEventList(EventType.COLOCALIZATION);
		
		System.out.println("Colocalization Events -> "+colocalizations.size());
		
		handleColocalization(colocalizations);	
	}

	private void handleColocalization(List<Event> colocalizations) {
		ObjectTree3D obj1;
		ObjectTree3D obj2;
		for (Event event : colocalizations) {
			obj1 = event.getObjectSource();
			obj2 = event.getObjectTarget();
			obj1.addChild(obj2);
			obj2.setParent(obj1);
			context.addNewObjectId(obj1.getId(), obj2);
		}
	}

}