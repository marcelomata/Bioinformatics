package trackingSPT.events.eventhandler;

import mcib3d.geom.Object3D;
import trackingInterface.Action;
import trackingSPT.objects3D.TrackingContextSPT;

public abstract class EventHandlerAction implements Action {
	
	protected static final double DISTANCE = 3;
//	private static double DISTANCE = 6;

	protected TrackingContextSPT context;
	
	public EventHandlerAction(TrackingContextSPT context) {
		this.context = context;
	}
	
	protected double getMaxAxisBoundBox(Object3D object) {
		double x = object.getMainElongation();
//		double x = object.getXmax() - object.getXmin();
//		double y = object.getYmax() - object.getYmin();
//		double z = object.getZmax() - object.getZmin();
//		double ret = (x > y ? (x > z ? x : z) : (y > z ? y : z) / 8);
//		return ret > 20 ? 20 : ret;
		return x;
	}
	
	protected double getDistCenterMax(Object3D object) {
		double x = object.getMainElongation();
//		double y = object.getYmax() - object.getYmin();
//		double z = object.getZmax() - object.getZmin();
//		return x > y ? (x > z ? x : z) : (y > z ? y : z);
		return x;
	}
	
}