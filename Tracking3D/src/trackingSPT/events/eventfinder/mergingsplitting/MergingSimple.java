package trackingSPT.events.eventfinder.mergingsplitting;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import trackingSPT.events.Event;
import trackingSPT.events.EventMapItem;
import trackingSPT.events.enums.EventCause;
import trackingSPT.events.enums.EventType;
import trackingSPT.events.eventfinder.EventSeekerAction;
import trackingSPT.math.CostMatrix;
import trackingSPT.math.HungarianAlgorithm;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingContextSPT;

public class MergingSimple extends EventSeekerAction {

	public MergingSimple(TrackingContextSPT context) {
		super(context);
		context.addEventType(EventType.MERGING);
	}

	@Override
	public void execute() {
		List<Event> events = new ArrayList<Event>();
		List<ObjectTree3D> leftSourceObjects = this.context.getLeftSourceObjects();
		Map<ObjectTree3D, List<ObjectTree3D>> associations = this.context.getAssociationsMap();
		//If the number of elements in the frame t is bigger than in the frame t+1
		if(leftSourceObjects.size() > 0) {
			//Verify the closest object in the associations to the left source objects
			//Number of elements in leftObjects list is smaller, so calculate it as the first parameter
			CostMatrix matrix = new CostMatrix(leftSourceObjects.size(), associations.size());
			
			Set<ObjectTree3D> targetsKey = associations.keySet();
			ObjectTree3D obj1;
			ObjectTree3D obj2;
			double distance;
			int j;
			for (int i = 0; i < leftSourceObjects.size(); i++) {
				obj1 = leftSourceObjects.get(i);
				matrix.addObjectSource(obj1, i);
				j = 0;
				for (ObjectTree3D key : targetsKey) {
					if(key != obj1) {
						obj2 = associations.get(key).get(0);
						matrix.addObjectTarget(obj2, j);
						distance = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
						matrix.setCost(i, j, distance);
					}
					j++;
				}
			}
			
			HungarianAlgorithm lapSolver = new HungarianAlgorithm(matrix.getCosts());
			int []result = lapSolver.execute();
			Event event;
			for (int i = 0; i < result.length; i++) {
				if(result[i] != -1) {
					event = new Event(EventCause.MISSED);
					obj1 = matrix.getSource(i);
					obj2 = matrix.getTarget(result[i]);
					event.setObjectSource(obj1);
					event.setObjectTarget(obj2);
					event.setEventType(EventType.MERGING);
					events.add(event);
				}
			}
		}
		
		EventMapItem item = new EventMapItem(EventType.MERGING);
		item.addEventList(events);
		this.context.addEventItem(item);
	}

}
