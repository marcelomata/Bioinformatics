package trackingSTP.mcib3DAdapters;

import mcib3d.geom.Object3D;

public class Object3DSTP {

	private Object3D object;
	
	public Object3DSTP(Object3D object) {
		this.object = object;
	}
	
	public Object3D getObject() {
		return object;
	}

}
