package trackingSPT.events.eventfinder.association;


import java.util.ArrayList;
import java.util.List;

import trackingSPT.events.Event;
import trackingSPT.events.EventMapItem;
import trackingSPT.events.enums.EventCause;
import trackingSPT.events.enums.EventType;
import trackingSPT.math.AssociationFunctionTracking;
import trackingSPT.math.CostMatrix;
import trackingSPT.math.HungarianAlgorithm;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingContextSPT;

public class AssociationMinDistance extends AssociationSeeker {
	
	private AssociationFunctionTracking function;
	
	public AssociationMinDistance(TrackingContextSPT context) {
		super(context);
		context.addEventType(EventType.ASSOCIATION);
		this.function =  new AssociationFunctionTracking();
	}

	@Override
	public void execute() {
//		Objects3DPopulation object3DTPlus1 = this.context.getObjectNextFrame();
//		
//		List<ObjectTree3D> leftSourceObject3DList = context.getListLastObjects();
//		
//		System.out.println(leftSourceObject3DList.size() + " and " + object3DTPlus1.getNbObjects());
//		
//		List<Object3D> object3DListTarget = object3DTPlus1.getObjectsList();
//		List<ObjectTree3D> leftTargetObject3DList = new ArrayList<ObjectTree3D>();
		
		List<ObjectTree3D> leftTargetObject3DList = this.context.getLeftTargetObjects();
		List<ObjectTree3D> leftSourceObject3DList = this.context.getLeftSourceObjects();
		
		CostMatrix matrix = new CostMatrix(leftSourceObject3DList.size(), leftTargetObject3DList.size());
		
		findShortestDistance(leftSourceObject3DList, leftTargetObject3DList, matrix);
		
//		this.context.addAllLeftTargetObjects(leftTargetObject3DList);
//		this.context.addAllLeftSourceObjects(leftSourceObject3DList);
	}
	
	private void findShortestDistance(List<ObjectTree3D> source, List<ObjectTree3D> target, CostMatrix matrix) {
		List<Event> events = new ArrayList<Event>();
		List<ObjectTree3D> list1 = source;
		List<ObjectTree3D> list2 = target;
		
		if(list1.size() > 0 && list2.size() > 0) {
			computeCostsMatrix(matrix, list1, list2);
			findEvents(matrix, list1, list2, events);
		}
		
		EventMapItem item = new EventMapItem(EventType.ASSOCIATION);
		item.addEventList(events);
		context.addEventItem(item);
		System.out.println("Association events "+events.size());
	}

	private void findEvents(CostMatrix matrix, List<ObjectTree3D> source, List<ObjectTree3D> target, List<Event> events) {
		HungarianAlgorithm lapSolver = new HungarianAlgorithm(matrix.getCosts());
		// result index is the frame t and the value is the index to the linked object in the next frame
		int []result = lapSolver.execute();
		
		boolean targetLeft = false;
		source.clear();
		if(source.size() < target.size()) {
			targetLeft = true;
		} else {
			target.clear();
		}
		
		ObjectTree3D obj1;
		ObjectTree3D obj2;
		Event event;
		
		int j;
		for (int i = 0; i < result.length; i++) {
			obj1 = matrix.getSource(i);
			j = result[i];
			if(j != -1) {
				event = new Event(EventCause.MINOR_DISTANCE);
				obj2 = matrix.getTarget(j);
				context.addAssociation(obj1, obj2);
				context.addDistanceValue(obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint()));
				event.setObjectSource(obj1);
				event.setObjectTarget(obj2);
				events.add(event);
				
				//leave target objects unlinked in the target list
				if(targetLeft) {
					target.remove(obj2);
				}
			} else {
				//leave source objects unlinked in the source list
				source.add(obj1);
			}
		}
	}

	private void computeCostsMatrix(CostMatrix matrix, List<ObjectTree3D> list1, List<ObjectTree3D> list2) {
		ObjectTree3D obj1;
		ObjectTree3D obj2;
		double distance = Double.MAX_VALUE;
		for (int i = 0; i < list1.size(); i++) {
			obj1 = list1.get(i);
			matrix.addObjectSource(obj1, i);
			for (int j = 0; j < list2.size(); j++) {
				obj2 = list2.get(j);
				matrix.addObjectTarget(obj2, j);
				distance = function.calcDistance(obj1, obj2);
				matrix.setCost(i, j, distance);
			}
		}
	}
}
