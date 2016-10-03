package trackingInterface;

import mcib3d.geom.Objects3DPopulation;
import mcib3d.image3d.ImageInt;

public interface Frame {

	Objects3DPopulation getObject3D();
	
	ImageInt getZones();
	
}