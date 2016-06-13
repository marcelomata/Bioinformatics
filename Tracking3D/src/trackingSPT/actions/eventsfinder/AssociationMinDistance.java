package trackingSPT.actions.eventsfinder;


import java.util.ArrayList;
import java.util.List;

import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import trackingInterface.ObjectAction;
import trackingSPT.enums.EventCause;
import trackingSPT.enums.EventType;
import trackingSPT.math.CostMatrix;
import trackingSPT.objects.TemporalObject;
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
		
		List<TemporalObject> leftSourceObject3DList = result.getListLastObjects();
		
		System.out.println(leftSourceObject3DList.size() + " and " + object3DTPlus1.getNbObjects());
		
		List<Object3D> object3DListTarget = object3DTPlus1.getObjectsList();
		List<TemporalObject> leftTargetObject3DList = new ArrayList<TemporalObject>();
		CostMatrix matrix = new CostMatrix(leftSourceObject3DList.size(), object3DListTarget.size());
		
		
		for (Object3D object3d : object3DListTarget) {
			leftTargetObject3DList.add(new TemporalObject(object3d));
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
				event.setObjectSource(min);
				event.setObjectTarget(obj1);
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
