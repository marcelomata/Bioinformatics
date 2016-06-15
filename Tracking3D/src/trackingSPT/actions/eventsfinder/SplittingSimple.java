package trackingSPT.actions.eventsfinder;


import java.util.ArrayList;
import java.util.List;

import trackingInterface.ObjectAction;
import trackingSPT.enums.EventCause;
import trackingSPT.enums.EventType;
import trackingSPT.math.CostMatrix;
import trackingSPT.math.HungarianAlgorithm;
import trackingSPT.objects.ObjectTree;
import trackingSPT.objects.events.Event;
import trackingSPT.objects.events.EventMapItem;

public class SplittingSimple extends SplittingMergingSeeker {

	@Override
	public ObjectAction execute() {
		this.events = new ArrayList<Event>();
		EventMapItem eventItem = new EventMapItem(EventType.SPLITTING);
		List<ObjectTree> leftTargetObjects = this.objectAction.getLeftTargetObjects();
		List<ObjectTree> associationsSources = this.objectAction.getAssociationsMapSources();
//		double max = 0; //TODO
		//If the number of elements in the frame t+1 is bigger than in the frame t
		if(leftTargetObjects.size() > 0) {
			//Verify the closest object in the associations to the left target objects
			//Number of elements in leftObjects list is smaller, so calculate it as the first parameter
			CostMatrix matrix = new CostMatrix(leftTargetObjects.size(), associationsSources.size());
			
			ObjectTree obj1;
			ObjectTree obj2;
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
			// and the value in result is the index to the source object
			int []result = lapSolver.execute();
			Event event;
			for (int i = 0; i < result.length; i++) {
				event = new Event(EventCause.EXCEEDED);
				// The order was changed because the list of left target is smaller
				obj1 = matrix.getTarget(result[i]);
				obj2 = matrix.getSource(i);
//				distance = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint()); //TODO
//				if (distance > max) {
//					max = distance;
//				}
//				System.out.print(distance+" ");
				event.setObjectSource(obj1);
				event.setObjectTarget(obj2);
				event.setEventType(EventType.SPLITTING);
				events.add(event);
			}
		}
		
//		System.out.println("\n MAX = "+max+"\n\n###"); //TODO
		associationsSources.clear();
		leftTargetObjects.clear();
		
		eventItem.addEventList(events);
		
		return eventItem;
	}
}
