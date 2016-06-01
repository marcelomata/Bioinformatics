package trackingSTP;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mcib3d.geom.Object3D;

public class MotionField {
	
	private Map<Integer, List<Object3D>> mapObjects;
	
	public MotionField() {
		this.mapObjects = new HashMap<Integer, List<Object3D>>();
	}
	
	public Map<Integer, List<Object3D>> getMapObjects() {
		return mapObjects;
	}

}
