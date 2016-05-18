import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public class TrackingCells2D {
	
	private List<Frame> video;
	private Map<Integer, Particle> particlesMap;
	private Map<Integer, Motion> tracking;
	private int currentFrame;
	
	private static int id = 1;
	
	public TrackingCells2D(List<BufferedImage> images) {
		loadVideo(images);
		this.currentFrame = 0;
		processCurrentFrame();
	}

	private void processCurrentFrame() {
		Frame frame = video.get(currentFrame);
		List<Particle> particles = frame.getParticles();
		if(currentFrame == 0) {
			for (Particle particle : particles) {
				particlesMap.put(id, particle);
				tracking.put(id, new Motion(particle.getParticlePosition()));
			}
		}
	}

	private void loadVideo(List<BufferedImage> images) {
		for (BufferedImage image : images) {
			video.add(new Frame(image));
		}
	}
	
}
