package trackingPlugin;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cell3DRenderer.Particle;
import cell3DRenderer.ParticlesObjects;
import mcib3d.geom.Point3D;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingResult3DSPT;

public class ParticlesTrackingResult implements ParticlesObjects {
	
	private TrackingResult3DSPT trackingResult;
	
	private Map<Integer, Color> particleColor;
	private List<Integer>  colorUsed;
	
	public ParticlesTrackingResult(TrackingResult3DSPT result) {
		this.trackingResult = result;
		
		this.particleColor = new HashMap<Integer, Color>();
		this.colorUsed = new ArrayList<Integer>();
		Set<Integer> keys = trackingResult.getFinalResultByTrack().keySet();
		
		Random random = new Random();
		Color color;
		float r;
		float g;
		float b;
		
		for (Integer integer : keys) {
			r = random.nextFloat();
			g = random.nextFloat();
			b = random.nextFloat();
			color = new Color(r, g, b);
			colorUsed.add(color.getRGB());
			particleColor.put(integer, color);
		}
	}

	@Override
	public Map<Integer, List<Particle>> getObjectsListTrack() {
		Map<Integer, List<ObjectTree3D>> objects = trackingResult.getFinalResultByTrack();
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
				particle = new Particle(objectTree.getObject(), integer, particleColor.get(integer));
				parent = objectTree.getParent();
				if(parent != null) {
					particle.setParent(new Particle(parent.getObject(), integer, particleColor.get(integer)));
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
	public List<List<Particle>> getObjectsListFrame() {
		int numberOfFrames = trackingResult.getNumberOfFrames();
		List<List<Particle>> result = new ArrayList<List<Particle>>(numberOfFrames);
		for (int i = 0; i < numberOfFrames; i++) {
			result.add(new ArrayList<Particle>());
		}
		
		List<List<ObjectTree3D>> objects = trackingResult.getFinalResultByFrame();
		
		List<Particle> particleList;
		Particle particle;
		ObjectTree3D parent;
		int frame;
		
		for (List<ObjectTree3D> frameObjects : objects) {
			for (ObjectTree3D objectTree : frameObjects) {
				particle = new Particle(objectTree.getObject(), objectTree.getId(), particleColor.get(objectTree.getId()));
				frame = objectTree.getFrame();
				particleList = result.get(frame);
				particleList.add(particle);
				parent = objectTree.getParent();
				if(parent != null) {
					particle.setParent(new Particle(parent.getObject(), objectTree.getId(), Color.WHITE));
				} else {
					particle.setParent(null);
				}
			}
		}
		
		print(result);
		
		return result; 
	}

	private void print(List<List<Particle>> result) {
		int frame = 0;
		Particle parent;
		for (List<Particle> list : result) {
			System.out.println("Frame "+frame+" - Particles "+list.size());
			for (Particle particle : list) {
				parent = particle.getParent();
				System.out.print("---> particle track"+particle.getTrack()+" ");
				if(parent != null) {
					System.out.println("---> parent track"+parent.getTrack()+"");
				}
			}
			frame++;
		}
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
