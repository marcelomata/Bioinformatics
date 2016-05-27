package trackingSTP.impl;

import mcib3d.geom.Objects3DPopulation;
import trackingInterface.ObjectAction;

public class Objects3DPopulationAdapter implements ObjectAction {
	
	private Objects3DPopulation object3D;
	
	public Objects3DPopulationAdapter(Objects3DPopulation object3D) {
		this.object3D = object3D;
	}
	
	public Objects3DPopulation getObject3D() {
		return object3D;
	}

}
