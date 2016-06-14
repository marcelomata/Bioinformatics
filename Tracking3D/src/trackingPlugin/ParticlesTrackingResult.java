package trackingPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cell3DRenderer.Particle;
import cell3DRenderer.ParticlesObjects;
import mcib3d.geom.Object3D;
import trackingSPT.objects.TrackingResultSPT;

public class ParticlesTrackingResult implements ParticlesObjects {
	
	private TrackingResultSPT trackingResult;
	
	public ParticlesTrackingResult(TrackingResultSPT result) {
		this.trackingResult = result;
	}

	@Override
	public Map<Integer, List<Particle>> getObjectsListId() {
		Map<Integer, List<Object3D>> objects = trackingResult.getMotionField().getFinalResult();
		Map<Integer, List<Particle>> particles = new HashMap<Integer, List<Particle>>();
		Set<Integer> objectKeys = objects.keySet();
		List<Particle> particleList;
		List<Object3D> objectsList;
		
		for (Integer integer : objectKeys) {
			objectsList = objects.get(integer);
			particleList = new ArrayList<Particle>();
			for (Object3D object3d : objectsList) {
				particleList.add(new Particle(object3d));
			}
			particles.put(integer, particleList);
		}
		
		return particles; 
	}

}
