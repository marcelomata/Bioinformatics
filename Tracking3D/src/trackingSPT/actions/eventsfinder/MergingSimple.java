package trackingSPT.actions.eventsfinder;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import trackingSPT.enums.EventCause;
import trackingSPT.enums.EventType;
import trackingSPT.math.CostMatrix;
import trackingSPT.math.HungarianAlgorithm;
import trackingSPT.objects.ObjectTree;
import trackingSPT.objects.events.AssociatedObjectList;
import trackingSPT.objects.events.Event;

public class MergingSimple extends EventSeekerAction {

	public MergingSimple(AssociatedObjectList associations) {
		super(associations, EventType.MERGING);
	}

	@Override
	public void execute() {
		this.events = new ArrayList<Event>();
		List<ObjectTree> leftSourceObjects = this.associations.getLeftSourceObjects();
		Map<ObjectTree, List<ObjectTree>> associations = this.associations.getAssociationsMap();
		//If the number of elements in the frame t is bigger than in the frame t+1
		if(leftSourceObjects.size() > 0) {
			//Verify the closest object in the associations to the left source objects
			//Number of elements in leftObjects list is smaller, so calculate it as the first parameter
			CostMatrix matrix = new CostMatrix(leftSourceObjects.size(), associations.size());
			
			Set<ObjectTree> targetsKey = associations.keySet();
			ObjectTree obj1;
			ObjectTree obj2;
			double distance;
			int j;
			for (int i = 0; i < leftSourceObjects.size(); i++) {
				obj1 = leftSourceObjects.get(i);
				matrix.addObjectSource(obj1, i);
				j = 0;
				for (ObjectTree key : targetsKey) {
					if(key != obj1) {
						obj2 = associations.get(key).get(0);
						matrix.addObjectTarget(obj2, j);
						distance = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
						matrix.setCost(i, j, distance);
					}
				}
			}
			
			HungarianAlgorithm lapSolver = new HungarianAlgorithm(matrix.getCosts());
			int []result = lapSolver.execute();
			Event event;
			for (int i = 0; i < result.length; i++) {
				event = new Event(EventCause.EXCEEDED);
				obj1 = matrix.getSource(i);
				obj2 = matrix.getTarget(result[i]);
				event.setObjectSource(obj1);
				event.setObjectTarget(obj2);
				event.setEventType(EventType.SPLITTING);
				events.add(event);
			}
		}
		
		eventItem.addEventList(events);
	}

}
