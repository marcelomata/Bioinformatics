package trackingSPT.objects;

import java.util.List;
import java.util.Map;

import mcib3d.geom.Object3D;
import trackingSPT.math.CostMatrix;

public abstract class EventSeekerObjectAction extends ObjectActionSPT {

	public abstract Map<Object3D, List<Object3D>> getAssociationsMap();
	
	public abstract List<Object3D> getLeftTargetObjects();
	
	public abstract List<Object3D> getLeftSourceObjects();
	
	public abstract CostMatrix getCostMatrix();

}
