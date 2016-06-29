package cell3DRenderer;

import java.util.List;
import java.util.Map;

import mcib3d.geom.Point3D;

public interface ParticlesObjects {
	
	Map<Integer,List<Particle>> getObjectsListId();
	
	Point3D getMaxPoint();
	
	Point3D getMinPoint();

}
