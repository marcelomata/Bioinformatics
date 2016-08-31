package trackingSPT.events.eventfinder;


import java.util.ArrayList;
import java.util.List;

import mcib3d.geom.Object3DPoint;
import mcib3d.geom.Point3D;
import trackingPlugin.Log;
import trackingSPT.events.Event;
import trackingSPT.events.EventMapItem;
import trackingSPT.events.enums.EventCause;
import trackingSPT.events.enums.EventType;
import trackingSPT.math.FunctionBorderDistance;
import trackingSPT.math.FunctionCalcObjectRelation;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingContextSPT;

public class GoingInAssociation extends EventSeekerAction {
	
	private FunctionCalcObjectRelation function;
	
	public GoingInAssociation(TrackingContextSPT context) {
		super(context);
		context.addEventType(EventType.GOING_IN);
		this.function =  new FunctionBorderDistance();
	}

	@Override
	public void execute() {
		List<Event> events = new ArrayList<Event>();
		List<ObjectTree3D> leftTargetObjects = this.context.getLeftTargetObjects();
		
		checkBoundBox(leftTargetObjects);
		
		double height = this.context.getHeight();
		double widht = this.context.getWidht();
		double depth = this.context.getDepth();
		double distance;
		double distMax;
		Event event;
		ObjectTree3D objectTree3D;
		distMax = context.getMeanDistanceFrame();
		for (int i = 0; i < leftTargetObjects.size(); i++) {
			objectTree3D = leftTargetObjects.get(i);
			distance = function.calculate(objectTree3D, new ObjectTree3D(new Object3DPoint(1, new Point3D(widht, height, depth)), context.getFrameTime()));
			if(distance < distMax) {
				event = new Event(EventCause.GOING_IN, context.getFrameTime());
				event.setObjectSource(null);
				event.setObjectTarget(objectTree3D);
				events.add(event);
				leftTargetObjects.remove(objectTree3D);
			} 
		}
		
		EventMapItem item = new EventMapItem(EventType.GOING_IN);
		item.addEventList(events);
		context.addEventItem(item);
		Log.println("Going in events "+events.size());
	}
	
	private void checkBoundBox(List<ObjectTree3D> leftTargetObjects) {
		double height = this.context.getHeight();
		double widht = this.context.getWidht();
		double depth = this.context.getDepth();
		
		Point3D p;
		for (ObjectTree3D objectTree3D : leftTargetObjects) {
			p = objectTree3D.getObject().getCenterAsPoint();
			if(!(p.getY() <= height && p.getY() >= 0) || !(p.getX() <= widht && p.getX() >= 0)) {
				Log.println("Object "+objectTree3D.getId()+" out");
			} else {
				if(depth > 1) {
					if(!(p.getZ() <= depth && p.getZ() >= 0)) {
						Log.println("Object "+objectTree3D.getId()+" out");
					}
				}
			}
		}
	}

}
