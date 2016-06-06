package trackingSPT.actions;


import java.util.ArrayList;
import java.util.List;

import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import trackingInterface.ObjectAction;
import trackingSPT.enums.EventCause;
import trackingSPT.enums.EventType;
import trackingSPT.math.CostMatrix;
import trackingSPT.objects.AssociatedObjectList;
import trackingSPT.objects.Event;
import trackingSPT.objects.EventMapItem;
import trackingSPT.objects.TemporalObject;
import trackingSPT.objects.TrackingResultObjectAction;

public class AssociationMinDistance extends AssociationSeeker {

	@Override
	public ObjectAction execute() {
		Objects3DPopulation object3DT = objectAction.getObjectT().getObject3D();
		Objects3DPopulation object3DTPlus1 = objectAction.getObjectTPlus1().getObject3D();
		TrackingResultObjectAction trackingResult = objectAction.getResult();
		
		System.out.println(object3DT.getNbObjects() + " and " + object3DTPlus1.getNbObjects());
		
		List<Object3D> object3DListSource = object3DT.getObjectsList();
		List<Object3D> object3DListTarget = object3DTPlus1.getObjectsList();
		List<TemporalObject> leftTargetObject3DList = new ArrayList<TemporalObject>();
		List<TemporalObject> leftSourceObject3DList = new ArrayList<TemporalObject>();
		CostMatrix matrix = new CostMatrix(object3DListSource.size(), object3DListTarget.size());
		for (Object3D object3d : object3DListTarget) {
			leftTargetObject3DList.add(new TemporalObject(object3d));
		}
		boolean firstFrame = false;
		if(trackingResult.getMotionField().getMapSize() == 0) {
			firstFrame = true;
		}
		for (Object3D object3d : object3DListSource) {
			leftSourceObject3DList.add(new TemporalObject(object3d));
			if(firstFrame) {
				trackingResult.getMotionField().addNewObject(new TemporalObject(object3d));
			}
		}
		
		EventMapItem eventItem = new EventMapItem(EventType.ASSOCIATION);
		this.associations = findShortestDistance(leftSourceObject3DList, leftTargetObject3DList, matrix, eventItem);
		this.associations.setCostMatrix(matrix);
		this.associations.addAllLeftTargetObjects(leftTargetObject3DList);
		this.associations.addAllLeftSourceObjects(leftSourceObject3DList);
		
		return eventItem;
	}
	
	private AssociatedObjectList findShortestDistance(List<TemporalObject> source, List<TemporalObject> target, CostMatrix matrix, EventMapItem eventItem) {
		List<TemporalObject> list1 = target;
		List<TemporalObject> list2 = source;
		boolean sourceFirst = false;
		if(source.size() < target.size()) {
			list1 = source;
			list2 = target;
			sourceFirst = true;
			System.out.println("true");
		}
		
		AssociatedObjectList result = new AssociatedObjectList();
		
		Event event = null;
		TemporalObject obj1 = null;
		TemporalObject obj2 = null;
		TemporalObject min = null;
		double minDistance = Double.MAX_VALUE;
		double newDistance = Double.MAX_VALUE;
		
		for (int i = 0; i < list1.size(); i++) {
			obj1 = list1.get(i);
			for (int j = 0; j < list2.size(); j++) {
				obj2 = list2.get(j);
				newDistance = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
				if(sourceFirst) {
					matrix.addAssociation(obj1, obj2);
					matrix.setCost(i, j, newDistance);
				} else {
					matrix.addAssociation(obj2, obj1);
					matrix.setCost(j, i, newDistance);
				}
				if(newDistance < minDistance) {
					minDistance = newDistance;
					min = obj2;
				}
			}
			
			event = new Event(EventCause.MINOR_DISTANCE);
			
			list2.remove(min);
			if(sourceFirst) {
				result.addAssociation(obj1, min);
				event.setObjectSource(obj1);
				event.setObjectTarget(min);
				events.add(event);
			} else {
				result.addAssociation(min, obj1);
				event.setObjectSource(obj1);
				event.setObjectTarget(min);
				events.add(event);
			}
			min = null;
			minDistance = Double.MAX_VALUE;
		}
		list1.clear();
		eventItem.addEventList(events);
		
		return result;
	}

}
