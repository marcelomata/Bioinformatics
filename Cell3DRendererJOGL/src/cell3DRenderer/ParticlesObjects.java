package cell3DRenderer;

import java.util.List;
import java.util.Map;

import mcib3d.geom.Point3D;

public interface ParticlesObjects {
	
	/**
	 * 
	 * @return list of the List<Particle> per frame
	 */
	List<List<Particle>> getObjectsListFrame();
	
	/**
	 * 
	 * @return map of the track and the List<Particle> of the particle tracked
	 */
	Map<Integer, List<Particle>> getObjectsListTrack();
	
	/**
	 * 
	 * @return the list of particle roots
	 */
	List<Particle> getTreeRoots();
	
	Point3D getMaxPoint();
	
	Point3D getMinPoint();

}
