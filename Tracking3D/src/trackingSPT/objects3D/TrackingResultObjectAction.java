package trackingSPT.objects3D;

import java.util.List;

import mcib3d.geom.Point3D;
import trackingSPT.MotionField;

public abstract class TrackingResultObjectAction {
	
	protected MovieObjectAction objectAction;
	
	public TrackingResultObjectAction(MovieObjectAction inObject) {
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
	
	public abstract List<ObjectTree3D> getListLastObjects();
	
	public abstract void finishObjectTracking(ObjectTree3D obj);
	
	public abstract void addNewObject(ObjectTree3D obj);

	public abstract MotionField getMotionField();

	public abstract void setObjectMissed(ObjectTree3D objMissed);
	
	public abstract ObjectTree3D getLastObjId(int id);

	public abstract int numberMissedObjects();
	
}
