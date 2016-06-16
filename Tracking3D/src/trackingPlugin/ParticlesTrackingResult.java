package trackingPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cell3DRenderer.Particle;
import cell3DRenderer.ParticlesObjects;
import trackingSPT.objects.ObjectTree;
import trackingSPT.objects.TrackingResultSPT;

public class ParticlesTrackingResult implements ParticlesObjects {
	
	private TrackingResultSPT trackingResult;
	
	public ParticlesTrackingResult(TrackingResultSPT result) {
		this.trackingResult = result;
	}

	@Override
	public Map<Integer, List<Particle>> getObjectsListId() {
		Map<Integer, List<ObjectTree>> objects = trackingResult.getMotionField().getFinalResult();
		Map<Integer, List<Particle>> particles = new HashMap<Integer, List<Particle>>();
		Set<Integer> objectKeys = objects.keySet();
		List<Particle> particleList;
		List<ObjectTree> objectsList;
		Particle particle;
		ObjectTree parent;
		
		for (Integer integer : objectKeys) {
			objectsList = objects.get(integer);
			particleList = new ArrayList<Particle>();
			for (ObjectTree objectTree : objectsList) {
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

}
