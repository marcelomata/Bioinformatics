import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TrackingCells2D {
	
	private List<Frame> video;
	private Map<Integer, List<Particle>> particlesMap;
	private int currentFrame;
	
	private static int id = 1;
	
	public TrackingCells2D(List<BufferedImage> images) {
		this.video = new ArrayList<Frame>();
		this.particlesMap = new HashMap<Integer, List<Particle>>();
		loadVideo(images);
		this.currentFrame = 0;
		processCurrentFrame();
	}

	private void processCurrentFrame() {
		Frame frame = video.get(currentFrame);
		List<Particle> particles = frame.getParticles();
		List<Particle> particleMotion = null;
		if(currentFrame == 0) {
			for (Particle particle : particles) {
				particleMotion = new ArrayList<Particle>();
				particleMotion.add(particle);
				particlesMap.put(id, particleMotion);
				id++;
			}
			currentFrame++;
		}
	}

	private void loadVideo(List<BufferedImage> images) {
		for (BufferedImage image : images) {
			video.add(new Frame(image));
		}
	}
	
	public void trackCells() {
		/**
		 * This first version only works for videos with perfect conditions, 
		 * where there are no changing in the number of cells, no overlap, no cell division,
		 * no cell going in or out to the scene. The size changing and acceleration of cells can also make errors.
		 */
		int videoSize = video.size();
		Frame frame = null;
		while(currentFrame < videoSize) {
			frame = video.get(currentFrame);
			matchParticles(frame.getParticles());
			currentFrame++;
		}
	}

	private void matchParticles(List<Particle> particles) {
		Set<Integer> ids = particlesMap.keySet();
		Particle particle = null;
		List<Particle> particleMotion = null;
		Particle closest = null;
		for (Integer id : ids) {
			particleMotion = particlesMap.get(id);
			particle = particleMotion.get(currentFrame-1);
			closest = findClosest(particle, particles);
			particleMotion.add(closest);
		}
	}

	private Particle findClosest(Particle particle, List<Particle> particles) {
		Particle current = particles.get(0);
		Particle closest = current;
		double distance = particle.getParticlePosition().distance(current.getParticlePosition());
		double shortestDistance = distance;
		for (int i = 1; i < particles.size(); i++) {
			current = particles.get(i);
			distance = particle.getParticlePosition().distance(current.getParticlePosition());
			if(shortestDistance > distance) {
				shortestDistance = distance;
				closest = current;
			}
		}
		
		return closest;
	}

	
}
