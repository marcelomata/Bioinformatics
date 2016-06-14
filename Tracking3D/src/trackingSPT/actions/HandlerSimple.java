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
		handleMergings(mergings, result);
		result.fillFinishedTrackings();
		
		return result;
	}

	private void handleMergings(List<Event> mergings, TrackingResultSPT result) {
		TemporalObject obj = null;
		//overlap
		for (Event event : mergings) {
			obj = event.getObjectSource();
			
			result.finishObjectTracking(obj.getId());
//			result.addMissed(obj);
//			result.addNewObjectId(obj.getId(), new TemporalObject(null));
		}
	}

	private void handleSplitting(List<Event> splittings, TrackingResultSPT result) {
//		List<MissedObject> misses = result.getMisses();
//		int numMisses = misses.size();
//		if(numMisses > 0) {
//			reconnect(misses, splittings, result);
//		}
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
		double newDistance = Double.MAX_VALUE;
		TemporalObject obj1 = null;
		TemporalObject obj2 = null;
		Event event = null;
		MissedObject missed = null;
		if(numMisses > numSplittings) {
			MissedObject min = null;
			for (int i = 0; i < splittings.size();) {
				event = splittings.get(i);
				obj1 = event.getObjectTarget();
				min = misses.get(0);
				for (int j = 0; j < misses.size(); j++) {
					missed = misses.get(j);
					obj2 = missed.getObject();
					newDistance = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
					if(newDistance < minDistance) {
						minDistance = newDistance;
						min = missed;
					}
				}
				misses.remove(min);
				splittings.remove(event);
				result.addNewObjectId(min.getObject().getId(), obj1);
				minDistance = Double.MAX_VALUE;
			}
		} else {
			Event min = null;
			for (int i = 0; i < misses.size();) {
				missed = misses.get(i);
				obj1 = missed.getObject();
				for (int j = 0; j < splittings.size(); j++) {
					event = splittings.get(j);
					obj2 = event.getObjectTarget();
					newDistance = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
					if(newDistance < minDistance) {
						minDistance = newDistance;
						min = event;
					}
				}
				splittings.remove(min);
				misses.remove(missed);
				result.addNewObjectId(obj1.getId(), min.getObjectTarget());
				minDistance = Double.MAX_VALUE;
			}
		}
	}

}
