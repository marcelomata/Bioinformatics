package trackingSPT.actions;


import java.util.List;

import trackingInterface.ObjectAction;
import trackingSPT.enums.EventType;
import trackingSPT.objects.MissedObject;
import trackingSPT.objects.TemporalObject;
import trackingSPT.objects.TrackingResultSPT;
import trackingSPT.objects.events.Event;

public class HandlerSimple extends Handler {

	@Override
	public ObjectAction execute() {
		System.out.println("Association Events -> "+this.objectAction.getEventList(EventType.ASSOCIATION).size());
		System.out.println("Splitting Events -> "+this.objectAction.getEventList(EventType.SPLITTING).size());
		System.out.println("Merging Events -> "+this.objectAction.getEventList(EventType.MERGING).size());
		
		List<Event> associations = this.objectAction.getEventList(EventType.ASSOCIATION);
		List<Event> splittings = this.objectAction.getEventList(EventType.SPLITTING);
		List<Event> mergings = this.objectAction.getEventList(EventType.MERGING);
		
		TrackingResultSPT result = (TrackingResultSPT) this.objectAction.getResult();
		
		handleAssociations(associations, result);
		
		handleSplitting(splittings, result);
		
		//TODO
		//overlap
		for (Event event : mergings) {
			result.addNewObjectId(event.getObjectSource().getId(), event.getObjectTarget());
		}
		
		return result;
	}

	private void handleSplitting(List<Event> splittings, TrackingResultSPT result) {
		List<MissedObject> misses = result.getMisses();
		int numMisses = misses.size();
		if(numMisses > 0) {
			reconnect(misses, splittings, result);
		}
		TemporalObject source = null;
		TemporalObject target1 = null;
		TemporalObject target2 = null;
		//divisions
		for (Event event : splittings) {
			source = event.getObjectSource();
			target1 = event.getObjectTarget();
			target2 = result.getMotionField().removeLastObject(source.getId());
			result.finishObjectTracking(source.getId());
			result.addNewObject(target1);
			result.addNewObject(target2);
		}
	}

	private void handleAssociations(List<Event> associations, TrackingResultSPT result) {
		Event temp = null;
		for (int i = 0; i < associations.size(); i++) {
			temp = associations.get(i);
			result.addNewObjectId(temp.getObjectSource().getId(), temp.getObjectTarget());
		}
	}
	
	private void reconnect(List<MissedObject> misses, List<Event> splittings, TrackingResultSPT result) {
		int numSplittings = splittings.size();
		int numMisses = misses.size();
		
		double minDistance = Double.MAX_VALUE;
		TemporalObject obj1 = null;
		TemporalObject min = null;
		Event event = null;
		MissedObject missed = null;
		if(numMisses > numSplittings) {
			for (int i = 0; i < numSplittings; i++) {
				event = splittings.get(i);
				obj1 = event.getObjectTarget();
				min = misses.get(0).getObject();
				for (int j = 0; j < numMisses; j++) {
					missed = misses.get(j);
					minDistance = verifyMinDistance(minDistance, obj1, missed.getObject(), min);
				}
				misses.remove(min);
				splittings.remove(event);
				result.addNewObjectId(min.getId(), obj1);
			}
		} else {
			for (int i = 0; i < numMisses; i++) {
				missed = misses.get(i);
				obj1 = missed.getObject();
				for (int j = 0; j < numSplittings; j++) {
					event = splittings.get(j);
					minDistance = verifyMinDistance(minDistance, obj1, event.getObjectTarget(), min);	
				}
				misses.remove(missed);
				splittings.remove(event);
			}
		}
	}

	private double verifyMinDistance(double minDistance, TemporalObject obj1, TemporalObject obj2, TemporalObject min) {
		double newDistance = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
		if(newDistance < minDistance) {
			minDistance = newDistance;
			min = obj2;
		}
		return minDistance;
	}

}
