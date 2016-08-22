package trackingSPT.events.eventhandler;

import java.util.ArrayList;
import java.util.List;

import mcib3d.geom.Object3D;
import mcib3d.geom.Vector3D;
import trackingPlugin.Log;
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
		
		Log.println("Splitting Events -> "+splittings.size());
		
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
//		double difference;
		
		//divisions
		for (Event event : splittings) {
			obj1 = event.getObjectSource();
			obj2 = event.getObjectTarget();
			if(context.motionFieldContains(obj1)) {
				obj3 = context.removeLastObject(obj1.getId());

				distance1 = obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint());
				distance2 = obj1.getObject().getCenterAsPoint().distance(obj3.getObject().getCenterAsPoint());
//				difference = Math.abs(distance1-distance2);
				
//				if(difference < distance1*context.getMeanDistanceFrame() && difference < distance2*context.getMeanDistanceFrame()) {
				if(checkSplitting(obj1, obj2, obj3)) {
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
				if(!context.motionFieldContains(obj2)) {
					context.addNewObject(obj2);
				}
			}
		}
	}

	private boolean checkSplitting(ObjectTree3D obj1, ObjectTree3D obj2, ObjectTree3D obj3) {
		Vector3D tempVector3 = new Vector3D(obj1.getObject().getCenterAsPoint(), obj3.getObject().getCenterAsPoint());
		Vector3D tempVectorOpp3 = new Vector3D(obj3.getObject().getCenterAsPoint(), obj1.getObject().getCenterAsPoint());
		Vector3D tempVector2 = new Vector3D(obj1.getObject().getCenterAsPoint(), obj2.getObject().getCenterAsPoint());
		Vector3D tempVectorOpp2 = new Vector3D(obj2.getObject().getCenterAsPoint(), obj1.getObject().getCenterAsPoint());
//		Vector3D tempVectorOpp = new Vector3D(-tempVector.getX(), -tempVector.getY(), -tempVector.getZ());
		Object3D objTemp1 = obj1.getObject();
//		Object3D objTemp2 = obj2.getObject();
		Object3D objTemp3 = obj3.getObject();
//		ImageJStatic.drawImageObjects(objTemp1, context.getCurrentSegFrame(), context.getSegmentedDataDir().getParent(), "1");
		objTemp1.translate(tempVector2);
//		ImageJStatic.drawImageObjects(objTemp1, context.getCurrentSegFrame(), context.getSegmentedDataDir().getParent(), "2");
//		ImageJStatic.drawImageObjects(objTemp1, objTemp2, context.getCurrentSegFrame(), context.getSegmentedDataDir().getParent());
		objTemp1.translate(tempVector3.multiply(2));
//		ImageJStatic.drawImageObjects(objTemp1, context.getCurrentSegFrame(), context.getSegmentedDataDir().getParent(), "3");
//		objTemp1.translate(tempVector3);
		
//		Log.println(String.valueOf(obj1.getObject().getColoc(obj3.getObject())));
//		Log.println(String.valueOf(obj1.getObject().getColoc(obj2.getObject())));
		
		double vol2 = objTemp1.getVolumePixels();
        double vol3 = objTemp3.getVolumePixels();
        double coloc = objTemp1.getColoc(objTemp3);
        double norm = (vol2 + vol3 - coloc) / (vol2 + vol3);
        
//        ImageJStatic.drawImageObjects(objTemp1, objTemp3, context.getCurrentSegFrame(), context.getSegmentedDataDir().getParent());
//        ImageJStatic.drawImageObjects(objTemp1, context.getCurrentSegFrame(), context.getSegmentedDataDir().getParent(), "4");
//        ImageJStatic.drawImageObjects(objTemp2, context.getCurrentSegFrame(), context.getSegmentedDataDir().getParent(), "1");
//        ImageJStatic.drawImageObjects(objTemp3, context.getCurrentSegFrame(), context.getSegmentedDataDir().getParent(), "1");
        
        objTemp1.translate(tempVectorOpp3.multiply(2));
//        objTemp1.translate(tempVectorOpp3);
        objTemp1.translate(tempVectorOpp2);
		
		return norm < 1;
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