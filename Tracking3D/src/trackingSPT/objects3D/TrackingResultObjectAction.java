package trackingSPT.objects3D;

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
	
}
