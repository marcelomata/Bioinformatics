package trackingSPT.events.eventfinder;


import java.util.ArrayList;
import java.util.List;

import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import trackingSPT.events.Event;
import trackingSPT.events.EventMapItem;
import trackingSPT.events.enums.EventCause;
import trackingSPT.events.enums.EventType;
import trackingSPT.math.AssociationFunctionTracking;
import trackingSPT.math.CostMatrix;
import trackingSPT.math.FunctionCalcObjectRelation;
import trackingSPT.math.FunctionColocalization;
import trackingSPT.math.HungarianAlgorithm;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingContextSPT;

public class ColocalizationAssociation extends EventSeekerAction {
	
	private FunctionCalcObjectRelation function;
	
	public ColocalizationAssociation(TrackingContextSPT context) {
		super(context);
		context.addEventType(EventType.COLOCALIZATION);
		this.function =  new FunctionColocalization();
	}

	@Override
	public void execute() {
		Objects3DPopulation object3DTPlus1 = this.context.getObjectNextFrame();
		
		List<ObjectTree3D> leftSourceObject3DList = context.getListLastObjects();
		
		System.out.println(leftSourceObject3DList.size() + " and " + object3DTPlus1.getNbObjects());
		
		List<Object3D> object3DListTarget = object3DTPlus1.getObjectsList();
		List<ObjectTree3D> leftTargetObject3DList = new ArrayList<ObjectTree3D>();
//		CostMatrix matrix = new CostMatrix(leftSourceObject3DList.size(), object3DListTarget.size());
		
		for (Object3D object3d : object3DListTarget) {
			leftTargetObject3DList.add(new ObjectTree3D(object3d, context.getCurrentFrame()));
		}
		
//		checkBoundBox(leftSourceObject3DList, leftTargetObject3DList);
		
//		findShortestDistance(leftSourceObject3DList, leftTargetObject3DList, matrix);
		this.context.addAllLeftTargetObjects(leftTargetObject3DList);
		this.context.addAllLeftSourceObjects(leftSourceObject3DList);
	}
	
	private void findEvents(List<ObjectTree3D> source, List<ObjectTree3D> target, List<Event> events) {
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
		
//		int j;
//		for (int i = 0; i < result.length; i++) {
//			obj1 = matrix.getSource(i);
//			j = result[i];
//			if(j != -1) {
//				event = new Event(EventCause.MINOR_DISTANCE);
//				obj2 = matrix.getTarget(j);
//				context.addAssociation(obj1, obj2);
//				context.addDistanceValue(obj1.getObject().getCenterAsPoint().distance(obj2.getObject().getCenterAsPoint()));
//				event.setObjectSource(obj1);
//				event.setObjectTarget(obj2);
//				events.add(event);
//				
//				//leave target objects unlinked in the target list
//				if(targetLeft) {
//					target.remove(obj2);
//				}
//			} else {
//				//leave source objects unlinked in the source list
//				source.add(obj1);
//			}
//		}
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
