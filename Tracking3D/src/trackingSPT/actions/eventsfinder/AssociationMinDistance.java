package trackingSPT.actions.eventsfinder;


import java.util.ArrayList;
import java.util.List;

import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import trackingInterface.ObjectAction;
import trackingSPT.enums.EventCause;
import trackingSPT.enums.EventType;
import trackingSPT.math.CostMatrix;
import trackingSPT.math.HungarianAlgorithm;
import trackingSPT.objects.ObjectTree;
import trackingSPT.objects.TrackingResultSPT;
import trackingSPT.objects.events.AssociatedObjectList;
import trackingSPT.objects.events.Event;
import trackingSPT.objects.events.EventMapItem;

public class AssociationMinDistance extends AssociationSeeker {

	@Override
	public ObjectAction execute() {
		this.events = new ArrayList<Event>();
		Objects3DPopulation object3DTPlus1 = objectAction.getObjectTPlus1().getObject3D();
		TrackingResultSPT result = (TrackingResultSPT) objectAction.getResult();
		
		List<ObjectTree> leftSourceObject3DList = result.getListLastObjects();
		
		System.out.println(leftSourceObject3DList.size() + " and " + object3DTPlus1.getNbObjects());
		
		List<Object3D> object3DListTarget = object3DTPlus1.getObjectsList();
		List<ObjectTree> leftTargetObject3DList = new ArrayList<ObjectTree>();
		CostMatrix matrix = new CostMatrix(leftSourceObject3DList.size(), object3DListTarget.size());
		
		
		for (Object3D object3d : object3DListTarget) {
			leftTargetObject3DList.add(new ObjectTree(object3d));
		}
		
		EventMapItem eventItem = new EventMapItem(EventType.ASSOCIATION);
		this.associations = findShortestDistance(leftSourceObject3DList, leftTargetObject3DList, matrix, eventItem);
		this.associations.setCostMatrix(matrix);
		this.associations.addAllLeftTargetObjects(leftTargetObject3DList);
		this.associations.addAllLeftSourceObjects(leftSourceObject3DList);
		
		return eventItem;
	}
	
	private AssociatedObjectList findShortestDistance(List<ObjectTree> source, List<ObjectTree> target, CostMatrix matrix, EventMapItem eventItem) {
		List<ObjectTree> list1 = source;
		List<ObjectTree> list2 = target;
		
		AssociatedObjectList result = new AssociatedObjectList();
		
		computeCostsMatrix(matrix, list1, list2);
		findEvents(matrix, result, list1, list2);
		
		eventItem.addEventList(events);
		
		return result;
	}

	private void findEvents(CostMatrix matrix, AssociatedObjectList associations, List<ObjectTree> source, List<ObjectTree> target) {
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
		
		ObjectTree obj1;
		ObjectTree obj2;
		Event event;
		
		int j;
//		double distance; //TODO
//		double max = 0;
		for (int i = 0; i < result.length; i++) {
			obj1 = matrix.getSource(i);
			j = result[i];
			if(j != -1) {
				event = new Event(EventCause.MINOR_DISTANCE);
				obj2 = matrix.getTarget(j);
				associations.addAssociation(obj1, obj2);
//				distance = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());  //TODO
//				if (distance > max) {
//					max = distance;
//				}
//				if(distance > 20) {
//					System.out.print(distance+" ");
//					matrix.printDistancesSource(obj1);
//				}
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
		
//		for (int i = 0; i < target.size(); i++) { //TODO
//			matrix.printDistancesTarget(target.get(i));
//		}
//		System.out.println("\n MAX = "+max+"\n\n###"); //TODO
	}

	private void computeCostsMatrix(CostMatrix matrix, List<ObjectTree> list1, List<ObjectTree> list2) {
		ObjectTree obj1;
		ObjectTree obj2;
		double distance = Double.MAX_VALUE;
//		double max = 0;
		for (int i = 0; i < list1.size(); i++) {
			obj1 = list1.get(i);
			matrix.addObjectSource(obj1, i);
			for (int j = 0; j < list2.size(); j++) {
				obj2 = list2.get(j);
				matrix.addObjectTarget(obj2, j);
				distance = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
//				if (distance > max) {  //TODO
//					max = distance;
//				}
//				System.out.print(distance+" ");
				matrix.setCost(i, j, distance);
			}
		}
//		System.out.println("\n MAX = "+max+"\n\n###");  //TODO
	}

}
