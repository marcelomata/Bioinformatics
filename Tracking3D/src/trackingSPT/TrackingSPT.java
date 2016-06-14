package trackingSPT;


import trackingInterface.Action;
import trackingInterface.Object4D;
import trackingInterface.TrackingStrategy;
import trackingSPT.actions.HandlerSimple;
import trackingSPT.actions.eventsfinder.EventSeekerStrategyAction;
import trackingSPT.objects.ObjectActionSPT4D;
import trackingSPT.objects.TrackingResultSPT;
import trackingSPT.objects.events.EventHandlerObjectAction;
import trackingSPT.objects.events.EventSeekerObj;

public class TrackingSPT extends TrackingStrategy {
	
	public TrackingSPT(Object4D inObject) {
		super(inObject);
	}

	@Override
	public void build() {
		addTrackingAction(new EventSeekerStrategyAction());
		addTrackingAction(new HandlerSimple());
	}

	@Override
	public void run() {
		Action current = null;
		EventSeekerObj object3DToAssociate = null;
		EventHandlerObjectAction eventList = null;
		ObjectActionSPT4D inObject4D = (ObjectActionSPT4D)inObject;
		
//		while(inObject4D.getCurrentFrame() < inObject4D.getSize()) {
		while(inObject4D.getCurrentFrame() < 12) {
			System.out.println("Current Frame -> "+inObject4D.getCurrentFrame());
			object3DToAssociate = inObject4D.getAssociationLastResult(result);
			current = nextAction();
			current.setObject(object3DToAssociate);
			eventList = (EventHandlerObjectAction) current.execute();
			
//			printAssociationMap(associatedObjects);
//			System.out.println("\n");
//			printEventList(eventList);
			
			current = nextAction();
			current.setObject(eventList);
			result = (TrackingResultSPT) current.execute();
			inObject4D.nextFrame();
			System.out.println("################################");
		}
	}

//	private void printEventList(EventHandlerObjectAction eventList) {
//		for (Event event : eventList.getEventList()) {
//			System.out.println("Object "+event.getObject().getName()+" event type "+event.getEventType());
//		}
//		System.out.println("\n");
//	}
//
//	private void printAssociationMap(EventSeekerObjectAction associatedObjects) {
//		Map<Object3D, List<Object3D>> associationsMap = associatedObjects.getAssociationsMap();
//		Set<Object3D> keys = associationsMap.keySet();
//		int count = 1;
//		for (Object3D object3d : keys) {
//			System.out.print(count + " Object "+object3d.getName()+" associated to ");
//			List<Object3D> list = associationsMap.get(object3d);
//			for (Object3D object3d2 : list) {
//				System.out.print(object3d2.getName()+" - ");
//			}
//			System.out.println();
//			count++;
//			if(count > 10) {
//				break;
//			}
//		}
//	}

}
