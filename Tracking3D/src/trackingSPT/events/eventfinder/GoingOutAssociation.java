package trackingSPT.events.eventfinder;


import java.util.ArrayList;
import java.util.List;

import mcib3d.geom.Object3DPoint;
import mcib3d.geom.Point3D;
import trackingSPT.events.Event;
import trackingSPT.events.EventMapItem;
import trackingSPT.events.enums.EventCause;
import trackingSPT.events.enums.EventType;
import trackingSPT.math.FunctionBorderDistance;
import trackingSPT.math.FunctionCalcObjectRelation;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingContextSPT;

public class GoingOutAssociation extends EventSeekerAction {
	
	private FunctionCalcObjectRelation function;
	
	public GoingOutAssociation(TrackingContextSPT context) {
		super(context);
		context.addEventType(EventType.GOING_OUT);
		this.function =  new FunctionBorderDistance();
	}

	@Override
	public void execute() {
		List<Event> events = new ArrayList<Event>();
		List<ObjectTree3D> leftSourceObjects = this.context.getLeftSourceObjects();
		
		checkBoundBox(leftSourceObjects);
		
		double height = this.context.getHeight();
		double widht = this.context.getWidht();
		double depth = this.context.getDepth();
		double distance;
		double distMax;
		Event event;
		ObjectTree3D objectTree3D;
		for (int i = 0; i < leftSourceObjects.size(); i++) {
			objectTree3D = leftSourceObjects.get(i);
			distance = function.calculate(objectTree3D, new ObjectTree3D(new Object3DPoint(1, new Point3D(widht, height, depth)), context.getFrameTime()));
			distMax = context.getMeanDistanceFrame() * 4;
			if(distance < distMax) {
				event = new Event(EventCause.GOING_OUT);
				event.setObjectSource(null);
				event.setObjectTarget(objectTree3D);
				events.add(event);
				leftSourceObjects.remove(objectTree3D);
			}
		}
		
		EventMapItem item = new EventMapItem(EventType.GOING_OUT);
		item.addEventList(events);
		context.addEventItem(item);
		System.out.println("Going out events "+events.size());
	}
	
	private void checkBoundBox(List<ObjectTree3D> leftObjects) {
		double height = this.context.getHeight();
		double widht = this.context.getWidht();
		double depth = this.context.getDepth();
		
		Point3D p;
		for (ObjectTree3D objectTree3D : leftObjects) {
			p = objectTree3D.getObject().getCenterAsPoint();
			if(!(p.getY() <= height && p.getY() >= 0) || !(p.getX() <= widht && p.getX() >= 0)) {
				System.out.println("Object "+objectTree3D.getId()+" out");
			} else {
				if(depth > 1) {
					if(!(p.getZ() <= depth && p.getZ() >= 0)) {
						System.out.println("Object "+objectTree3D.getId()+" out");
					}
				}
			}
		}
	}

}
