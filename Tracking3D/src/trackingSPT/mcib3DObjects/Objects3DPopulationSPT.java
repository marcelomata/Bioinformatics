package trackingSPT.mcib3DObjects;

import mcib3d.geom.Objects3DPopulation;
import trackingInterface.Frame;

public class Objects3DPopulationSPT implements Frame {
	
	private Objects3DPopulation object3D;
	
	public Objects3DPopulationSPT(Objects3DPopulation object3D) {
		this.object3D = object3D;
	}
	
	public Objects3DPopulation getObject3D() {
		return object3D;
	}

}
