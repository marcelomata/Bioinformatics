package trackingSPT.events.eventhandler;


import java.util.ArrayList;
import java.util.List;

import trackingSPT.events.Event;
import trackingSPT.events.enums.EventType;
import trackingSPT.math.CostMatrix;
import trackingSPT.math.HungarianAlgorithm;
import trackingSPT.objects3D.MissedObject;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingContextSPT;
import trackingSPT.objects3D.TrackingResult3DSPT;

public class HandlerSimple extends Handler {
	
	private static double DISTANCE = 2;

	public HandlerSimple(TrackingContextSPT context) {
		super(context);
	}

	@Override
	public void execute() {
		List<Event> associations = this.context.getEventList(EventType.ASSOCIATION);
		List<Event> splittings = this.context.getEventList(EventType.SPLITTING);
		List<Event> mergings = this.context.getEventList(EventType.MERGING);
		
		System.out.println("Association Events -> "+associations.size());
		System.out.println("Splitting Events -> "+splittings.size());
		System.out.println("Merging Events -> "+mergings.size());
		
		TrackingResult3DSPT result = (TrackingResult3DSPT) this.context.getResult();
		
		handleAssociations(associations, result);
		handleSplitting(splittings, result);
		handleMergings(mergings, result);
		result.fillFinishedTrackings();
		addNullToMisses(context.getMisses());
		System.out.println(result.getMotionField().isDifferentNumber());
	}

	private void handleMergings(List<Event> mergings, TrackingResult3DSPT result) {
		ObjectTree3D obj1;
		ObjectTree3D obj2;
		double distance;
		//overlap
		for (Event event : mergings) {
			obj1 = event.getObjectSource();
			obj2 = event.getObjectTarget();
			distance = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
			//if the closest is near so a merge sould happened
			if(distance < (obj1.getObject().getDistCenterMax()*DISTANCE)) {
				context.addMissed(obj1);
			} else {
				result.finishObjectTracking(obj1);
			}
		}
	}

	private void handleSplitting(List<Event> splittings, TrackingResult3DSPT result) {
		List<MissedObject> misses = this.context.getMisses();
		reconnect(misses, splittings, result);
		ObjectTree3D obj1;
		ObjectTree3D obj2;
		ObjectTree3D obj3;
		double distance1;
		double distance2;
		double difference;
		
		//divisions
		for (Event event : splittings) {
			obj1 = event.getObjectSource();
			obj2 = event.getObjectTarget();
			if(result.getMotionField().contains(obj1)) {
				obj3 = result.getMotionField().removeLastObject(obj1.getId());

				distance1 = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
				distance2 = obj1.getObject().getCenterAsPoint().distance(obj3.getObject().getCenterAsPoint());
				difference = Math.abs(distance1-distance2);
				
				if(difference < distance1*3 && difference < distance2*DISTANCE) {
					result.finishObjectTracking(obj1);
					result.addNewObject(obj2);
					result.addNewObject(obj3);
					obj1.addChild(obj2);
					obj1.addChild(obj3);
					obj2.setParent(obj1);
					obj3.setParent(obj1);
				} else {
					if(distance1 < distance2) {
						result.addNewObject(obj3);
						obj1.addChild(obj2);
						obj2.setParent(obj1);
					} else {
						result.addNewObject(obj2);
						obj1.addChild(obj3);
						obj3.setParent(obj1);
					}
				}
			} else {
				result.addNewObject(obj2);
			}
			
		}
	}

	private void handleAssociations(List<Event> associations, TrackingResult3DSPT result) {
		Event temp = null;
		double distance;
		for (int i = 0; i < associations.size(); i++) {
			temp = associations.get(i);
			ObjectTree3D obj1 = temp.getObjectSource();
			ObjectTree3D obj2 = temp.getObjectTarget();
			distance = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
			double comp = obj1.getObject().getDistCenterMax();
			comp = Double.isNaN(comp) ? 3 : comp; 
			if(distance < (comp*3)) {
				result.addNewObjectId(obj1.getId(), obj2);
				obj2.setParent(obj1);
				obj1.addChild(obj2);
			} else {
				result.finishObjectTracking(obj1);
				result.addNewObject(obj2);
			}
		}
	}
	
	private void reconnect(List<MissedObject> misses, List<Event> splittings, TrackingResult3DSPT resultTracking) {
		int numSplittings = splittings.size();
		int numMisses = misses.size();
		
		if(numMisses > 0 && numSplittings > 0) {
		
			double distance;
			ObjectTree3D obj1 = null;
			ObjectTree3D obj2 = null;
			Event event;
			MissedObject missed;
			CostMatrix matrix;
			List<Event> eventsRemove = new ArrayList<Event>();
			List<MissedObject> missedRemove = new ArrayList<MissedObject>();
			if(numMisses > numSplittings) {
				matrix = new CostMatrix(numSplittings, numMisses);
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
					// Source in the matrix is the object in the frame t+1
					obj1 = matrix.getSource(i);
					obj2 = matrix.getTarget(result[i]);
					distance = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
					if(distance < (obj1.getObject().getDistCenterMax()*DISTANCE)) {
						resultTracking.addNewObjectId(obj2.getId(), obj1);
						obj1.setParent(obj2);
						obj2.addChild(obj1);
						missedRemove.add(misses.get(result[i]));
						eventsRemove.add(splittings.get(i));
					}
				}
			} else {
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
					distance = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
					if(distance < (obj1.getObject().getDistCenterMax()*DISTANCE)) {
						resultTracking.addNewObjectId(obj1.getId(), obj2);
						obj1.addChild(obj2);
						obj2.setParent(obj1);
						eventsRemove.add(splittings.get(result[i]));
						missedRemove.add(misses.get(i));
					}
				}
			}
			splittings.removeAll(eventsRemove);
			misses.removeAll(missedRemove);
		}
	}

	private void addNullToMisses(List<MissedObject> misses) {
		ObjectTree3D obj;
		ObjectTree3D nullObject;
		for (MissedObject missedObject : misses) {
			obj = missedObject.getObject();
			nullObject = new ObjectTree3D(null);
			context.addNewObjectId(obj.getId(), nullObject);
			obj.addChild(nullObject);
			nullObject.setParent(obj);
		}
	}
	
}