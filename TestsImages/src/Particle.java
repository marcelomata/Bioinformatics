import ij.gui.Roi;
import mcib3d.geom.Point3D;

public class Particle {
	
	private Roi roi;
	private ParticleState state;
	private Point3D particlePosition;
	
	public Particle(Roi roi) {
		this.roi = roi;
		double centroid[] = roi.getContourCentroid();
		this.particlePosition = new Point3D(centroid[0], centroid[1], 1.0);
		this.state = ParticleState.TRACKING;
	}
	
	public Roi getRoi() {
		return roi;
	}
	
	public Point3D getParticlePosition() {
		return particlePosition;
	}
	
}
