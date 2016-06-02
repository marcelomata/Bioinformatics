package trackingSPT.objects;

import java.util.List;

import mcib3d.geom.Object3D;

public abstract class SplittingObjectAction extends EventSeekerObj {
	
	public abstract List<Object3D> getLeftTargetObjects();
	
}