package trackingSPT.actions;


import java.util.ArrayList;
import java.util.List;

import trackingSPT.enums.EventType;
import trackingSPT.math.CostMatrix;
import trackingSPT.math.HungarianAlgorithm;
import trackingSPT.objects.MissedObject;
import trackingSPT.objects.ObjectTree;
import trackingSPT.objects.TrackingResultObjectAction;
import trackingSPT.objects.TrackingResultSPT;
import trackingSPT.objects.events.Event;
import trackingSPT.objects.events.EventHandlerObjectAction;

public class HandlerSimple extends Handler {

	public HandlerSimple(EventHandlerObjectAction eventList) {
		super(eventList);
	}

	@Override
	public void execute() {
		System.out.println("Association Events -> "+this.objectAction.getEventList(EventType.ASSOCIATION).size());
		System.out.println("Splitting Events -> "+this.objectAction.getEventList(EventType.SPLITTING).size());
		System.out.println("Merging Events -> "+this.objectAction.getEventList(EventType.MERGING).size());
		
		List<Event> associations = this.objectAction.getEventList(EventType.ASSOCIATION);
		List<Event> splittings = this.objectAction.getEventList(EventType.SPLITTING);
		List<Event> mergings = this.objectAction.getEventList(EventType.MERGING);
		
		TrackingResultSPT result = (TrackingResultSPT) this.objectAction.getResult();
		
		handleAssociations(associations, result);
		handleSplitting(splittings, result);
		handleMergings(mergings, result);
		result.fillFinishedTrackings();
	}

	private void handleMergings(List<Event> mergings, TrackingResultSPT result) {
		ObjectTree obj;
		ObjectTree nullObject;
		//overlap
		for (Event event : mergings) {
			obj = event.getObjectSource();
//			result.finishObjectTracking(obj.getId());
			result.addMissed(obj);
			nullObject = new ObjectTree(null);
			result.addNewObjectId(obj.getId(), nullObject);
			nullObject.setParent(obj);
			obj.addChild(nullObject);
		}
	}

	private void handleSplitting(List<Event> splittings, TrackingResultSPT result) {
		List<MissedObject> misses = result.getMisses();
		reconnect(misses, splittings, result);
		ObjectTree obj1 = null;
		ObjectTree obj2 = null;
		ObjectTree obj3 = null;
		//divisions
		for (Event event : splittings) {
			obj1 = event.getObjectSource();
			obj2 = event.getObjectTarget();
			obj3 = result.getMotionField().removeLastObject(obj1.getId());
			result.finishObjectTracking(obj1.getId());
			result.addNewObject(obj2);
			result.addNewObject(obj3);
			obj1.addChild(obj2);
			obj1.addChild(obj3);
			obj2.setParent(obj1);
			obj3.setParent(obj1);
		}
	}

	private void handleAssociations(List<Event> associations, TrackingResultSPT result) {
		Event temp = null;
		for (int i = 0; i < associations.size(); i++) {
			temp = associations.get(i);
			ObjectTree obj1 = temp.getObjectSource();
			ObjectTree obj2 = temp.getObjectTarget();
			result.addNewObjectId(obj1.getId(), obj2);
			obj2.setParent(obj1);
			obj1.addChild(obj2);
		}
	}
	
	private void reconnect(List<MissedObject> misses, List<Event> splittings, TrackingResultSPT resultTracking) {
		int numSplittings = splittings.size();
		int numMisses = misses.size();
		
		if(numMisses > 0 && numSplittings > 0) {
		
			double distance;
			ObjectTree obj1 = null;
			ObjectTree obj2 = null;
			Event event;
			MissedObject missed;
			CostMatrix matrix;
			if(numMisses > numSplittings) {
				matrix = new CostMatrix(numSplittings, numMisses);
				List<MissedObject> missedRemove = new ArrayList<MissedObject>();
				for (int i = 0; i < splittings.size(); i++) {
					event = splittings.get(i);
					obj1 = event.getObjectTarget();
					matrix.addObjectSource(obj1, i);
					for (int j = 0; j < misses.size(); j++) {
						missed = misses.get(j);
						obj2 = missed.getObject();
						matrix.addObjectTarget(obj2, j);
						distance = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
						matrix.setCost(i, j, distance);
					}
				}
				HungarianAlgorithm lapSolver = new HungarianAlgorithm(matrix.getCosts());
				int []result = lapSolver.execute();
				for (int i = 0; i < result.length; i++) {
					// Source in the matrix was the object in the frame t+1
					obj1 = matrix.getSource(i);
					obj2 = matrix.getTarget(result[i]);
					resultTracking.addNewObjectId(obj2.getId(), obj1);
					obj1.setParent(obj2);
					obj2.addChild(obj1);
					missedRemove.add(misses.get(result[i]));
				}
				misses.removeAll(missedRemove);
				splittings.clear();
			} else {
				List<Event> eventsRemove = new ArrayList<Event>();
				matrix = new CostMatrix(misses.size(), splittings.size());
				for (int i = 0; i < misses.size(); i++) {
					missed = misses.get(i);
					obj1 = missed.getObject();
					matrix.addObjectSource(obj1, i);
					for (int j = 0; j < splittings.size(); j++) {
						event = splittings.get(j);
						obj2 = event.getObjectTarget();
						matrix.addObjectTarget(obj2, j);
						distance = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
					}
				}
				HungarianAlgorithm lapSolver = new HungarianAlgorithm(matrix.getCosts());
				int []result = lapSolver.execute();
				for (int i = 0; i < result.length; i++) {
					// Source in the matrix was the object in the frame t+1
					obj1 = matrix.getSource(i);
					obj2 = matrix.getTarget(result[i]);
					resultTracking.addNewObjectId(obj1.getId(), obj2);
					obj1.addChild(obj2);
					obj2.setParent(obj1);
					eventsRemove.add(splittings.get(result[i]));
				}
				splittings.removeAll(eventsRemove);
				misses.clear();
			}
		}
	}
	
	@Override
	public EventHandlerObjectAction getEventHandler() {
		return objectAction;
	}

	@Override
	public TrackingResultObjectAction getResult() {
		return this.objectAction.getResult();
	}

}

	
