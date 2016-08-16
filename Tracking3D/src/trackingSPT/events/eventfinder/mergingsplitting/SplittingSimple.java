package trackingSPT.events.eventfinder.mergingsplitting;


import java.util.ArrayList;
import java.util.List;

import trackingPlugin.Log;
import trackingSPT.events.Event;
import trackingSPT.events.EventMapItem;
import trackingSPT.events.enums.EventCause;
import trackingSPT.events.enums.EventType;
import trackingSPT.events.eventfinder.EventSeekerAction;
import trackingSPT.math.CostMatrix;
import trackingSPT.math.HungarianAlgorithm;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingContextSPT;

public class SplittingSimple extends EventSeekerAction {

	public SplittingSimple(TrackingContextSPT context) {
		super(context);
		context.addEventType(EventType.SPLITTING);
	}
	
	@Override
	public void execute() {
		List<Event> events = new ArrayList<Event>();
		List<ObjectTree3D> leftTargetObjects = this.context.getLeftTargetObjects();
		List<ObjectTree3D> associationsSources = this.context.getAssociationsMapSources();
		//If the number of elements in the frame t+1 is bigger than in the frame t
		if(leftTargetObjects.size() > 0) {
			//Verify the closest object in the associations to the left target objects
			//Number of elements in leftObjects list is smaller, so calculate it as the first parameter
			CostMatrix matrix = new CostMatrix(leftTargetObjects.size(), associationsSources.size());
			
			ObjectTree3D obj1;
			ObjectTree3D obj2;
			double distance;
			for (int i = 0; i < leftTargetObjects.size(); i++) {
				obj1 = leftTargetObjects.get(i);
				matrix.addObjectSource(obj1, i);
				for (int j = 0; j < associationsSources.size(); j++) {
					obj2 = associationsSources.get(j);
					matrix.addObjectTarget(obj2, j);
					distance = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
					matrix.setCost(i, j, distance);
				}
			}
			
			HungarianAlgorithm lapSolver = new HungarianAlgorithm(matrix.getCosts());
			// The index of a result`s element is to the left target object 
			// and the value in result is the indsplittingex to the source object
			int []result = lapSolver.execute();
			Event event;
			for (int i = 0; i < result.length; i++) {
				if(result[i] != -1) { 
					event = new Event(EventCause.EXCEEDED);
					// The order was changed because the list of left target is smaller
					obj1 = matrix.getTarget(result[i]);
					obj2 = matrix.getSource(i);
					event.setObjectSource(obj1);
					event.setObjectTarget(obj2);
					event.setEventType(EventType.SPLITTING);
					events.add(event);
				}
			}
		}
		
		associationsSources.clear();
		leftTargetObjects.clear();
		
		EventMapItem item = new EventMapItem(EventType.SPLITTING);
		item.addEventList(events);
		this.context.addEventItem(item);
		Log.println("Splitting events "+events.size());
	}
}
