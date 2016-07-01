package trackingPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cell3DRenderer.Particle;
import cell3DRenderer.ParticlesObjects;
import mcib3d.geom.Point3D;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingResult3DSPT;

public class ParticlesTrackingResult implements ParticlesObjects {
	
	private TrackingResult3DSPT trackingResult;
	
	public ParticlesTrackingResult(TrackingResult3DSPT result) {
		this.trackingResult = result;
	}

	@Override
	public Map<Integer, List<Particle>> getObjectsListId() {
		Map<Integer, List<ObjectTree3D>> objects = trackingResult.getMotionField().getFinalResult();
		Map<Integer, List<Particle>> particles = new HashMap<Integer, List<Particle>>();
		Set<Integer> objectKeys = objects.keySet();
		List<Particle> particleList;
		List<ObjectTree3D> objectsList;
		Particle particle;
		ObjectTree3D parent;
		
		for (Integer integer : objectKeys) {
			objectsList = objects.get(integer);
			particleList = new ArrayList<Particle>();
			for (ObjectTree3D objectTree : objectsList) {
				particle = new Particle(objectTree.getObject());
				parent = objectTree.getParent();
				if(parent != null) {
					particle.setParent(new Particle(parent.getObject()));
				} else {
					particle.setParent(null);
				}
				particleList.add(particle);
			}
			particles.put(integer, particleList);
		}
		
		return particles; 
	}

	@Override
	public Point3D getMaxPoint() {
		return trackingResult.getMaxPoint();
	}

	@Override
	public Point3D getMinPoint() {
		return trackingResult.getMinPoint();
	}

}
