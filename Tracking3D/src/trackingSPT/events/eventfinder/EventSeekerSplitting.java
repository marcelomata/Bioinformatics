package trackingSPT.events.eventfinder;


import java.util.ArrayList;
import java.util.List;

import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import trackingPlugin.Log;
import trackingSPT.events.Event;
import trackingSPT.events.EventMapItem;
import trackingSPT.events.enums.EventCause;
import trackingSPT.events.enums.EventType;
import trackingSPT.math.FunctionCalcObjectRelation;
import trackingSPT.math.FunctionSplittingMergingColocalization;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.SplittedObject;
import trackingSPT.objects3D.TrackingContextSPT;

/**
 * 
 * Find events of duplication, that happen when 2 particle in the frame t+1 are completely inside of the parent.
 * It happens when the segmentation algorithm can not segment the particle properly in the frame t+1.
 * 
 * @author Marcelo da Mata
 *
 */
public class EventSeekerSplitting extends EventSeekerAction {
	
	private FunctionCalcObjectRelation function;
	
	public EventSeekerSplitting(TrackingContextSPT context) {
		super(context);
		context.addEventType(EventType.SPLITTING);
		this.function =  new FunctionSplittingMergingColocalization();
	}

	@Override
	public void execute() {
		Objects3DPopulation object3DTPlus1 = this.context.getObjectNextFrame();
		
		List<Event> events = new ArrayList<Event>();
		List<ObjectTree3D> leftSourceObjects = context.getListLastObjects();
		
		List<Object3D> object3DListTarget = object3DTPlus1.getObjectsList();
		
		Log.println(leftSourceObjects.size() + " and " + object3DTPlus1.getNbObjects());
		
		List<ObjectTree3D> leftTargetObjects = new ArrayList<ObjectTree3D>();
		
		for (Object3D object3d : object3DListTarget) {
			leftTargetObjects.add(new ObjectTree3D(object3d, context.getCurrentFrame()));
		}
		
		//If the number of elements in the frame t is bigger than in the frame t+1
		if(leftTargetObjects.size() > 0) {
			ObjectTree3D source;
			double distance;
			Event event;
			SplittedObject splittedObject;
			List<ObjectTree3D> targets ;
			for (int i = 0; i < leftSourceObjects.size(); i++) {
				source = leftSourceObjects.get(i);
				splittedObject = new SplittedObject(source, context.getFrameTime());
				for (ObjectTree3D target : leftTargetObjects) {
						distance = function.calculate(source, target);
						if(distance <= 1.00) {
							splittedObject.addTarget(target);
						}
				}
				targets = splittedObject.getTargetObjects();
				//If the object splitted in 2 or more objects
				//So create the splitting events
				if(targets.size() > 1) {
					for (ObjectTree3D splittedTarget : targets) {
//						Log.println(source.getObject().getCenterAsPoint()+" - "+splittedTarget.getObject().getCenterAsPoint());
//						Log.println(""+function.calculate(source, splittedTarget));
						event = new Event(EventCause.EXCEEDED, context.getFrameTime());
						event.setObjectSource(source);
						event.setObjectTarget(splittedTarget);
						event.setEventType(EventType.SPLITTING);
						events.add(event);
						leftTargetObjects.remove(splittedTarget);
					}
					leftSourceObjects.remove(source);
					i--;
				}
			}
			
		}
		
		EventMapItem item = new EventMapItem(EventType.SPLITTING);
		item.addEventList(events);
		this.context.addEventItem(item);
		this.context.addAllLeftTargetObjects(leftTargetObjects);
		this.context.addAllLeftSourceObjects(leftSourceObjects);
		Log.println("Splitting events "+events.size());
		
	}
	
}
