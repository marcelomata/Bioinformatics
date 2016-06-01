package trackingSTP.objects;

import java.util.List;
import java.util.Map;

import mcib3d.geom.Object3D;
import trackingInterface.ObjectAction;
import trackingSTP.math.CostMatrix;

public abstract class EventSeekerObjectAction implements ObjectAction {

	public abstract Map<Object3D, List<Object3D>> getAssociationsMap();
	
	public abstract List<Object3D> getLeftTargetObjects();
	
	public abstract List<Object3D> getLeftSourceObjects();
	
	public abstract CostMatrix getCostMatrix();

}
