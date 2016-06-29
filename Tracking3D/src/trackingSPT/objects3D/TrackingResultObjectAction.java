package trackingSPT.objects3D;

import mcib3d.geom.Point3D;
import trackingInterface.ObjectAction;

public abstract class TrackingResultObjectAction implements ObjectAction {
	
	protected MovieObjectAction objectAction;
	
	public TrackingResultObjectAction(ObjectActionSPT4D inObject) {
		this.objectAction = inObject;
		init();
	}
	
	public int getCurrentFrame() {
		return this.objectAction.getFrameTime();
	}
	
	abstract void addNewObjectId(Integer id, ObjectTree3D treeObj);

	public abstract void init();
	
	public abstract Point3D getMaxPoint();
	
	public abstract Point3D getMinPoint();
	
}
