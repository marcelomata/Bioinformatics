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

public class HandlerSplitting extends EventHandlerAction {
	
	public HandlerSplitting(TrackingContextSPT context) {
		super(context);
	}

	@Override
	public void execute() {
		List<Event> splittings = this.context.getEventList(EventType.SPLITTING);
		
		System.out.println("Splitting Events -> "+splittings.size());
		
		handleSplitting(splittings);
	}

	private void handleSplitting(List<Event> splittings) {
		List<MissedObject> misses = this.context.getMisses();
		reconnect(misses, splittings);
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
			if(context.motionFieldContains(obj1)) {
				obj3 = context.removeLastObject(obj1.getId());

				distance1 = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
				distance2 = obj1.getObject().getCenterAsPoint().distance(obj3.getObject().getCenterAsPoint());
				difference = Math.abs(distance1-distance2);
				
				if(difference < distance1*context.getMeanDistanceFrame() && difference < distance2*context.getMeanDistanceFrame()) {
					context.finishObjectTracking(obj1);
					obj1.removeChildren();
					obj1.addChild(obj2);
					obj1.addChild(obj3);
					obj2.setParent(obj1);
					obj3.setParent(obj1);
					context.addNewObject(obj2);
					context.addNewObject(obj3);
				} else {
					if(distance1 < distance2) {
						context.addNewObject(obj3);
						obj1.addChild(obj2);
						obj2.setParent(obj1);
						context.addNewObjectId(obj1.getId(), obj2);
					} else {
						context.addNewObject(obj2);
						obj1.addChild(obj3);
						obj3.setParent(obj1);
						context.addNewObjectId(obj1.getId(), obj3);
					}
				}
			} else {
				context.addNewObject(obj2);
			}
		}
	}

	private void reconnect(List<MissedObject> misses, List<Event> splittings) {
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
					if(distance < (getMaxAxisBoundBox(obj1.getObject())*context.getMeanDistanceFrame())) {
						context.reconnectMissedObject(obj2.getId());
						context.addNewObjectId(obj2.getId(), obj1);
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
					if(distance < (getMaxAxisBoundBox(obj1.getObject())*context.getMeanDistanceFrame())) {
						context.reconnectMissedObject(obj1.getId());
						context.addNewObjectId(obj1.getId(), obj2);
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

}