import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ParticlesMatrix {
	
	private Edge [][]particlesMatrix;
	private List<LinkedList<Edge>> orderedLists = null;
	
	public ParticlesMatrix(List<Particle> particlesFrame1, List<Particle> particlesFrame2) {
		this.particlesMatrix = new Edge[particlesFrame1.size()][particlesFrame2.size()];
		this.orderedLists = new ArrayList<LinkedList<Edge>>(particlesFrame1.size());
		loadMatrix(particlesFrame1, particlesFrame2);
	}

	private void loadMatrix(List<Particle> particlesFrame1, List<Particle> particlesFrame2) {
		Particle particle1 = null;
		Particle particle2 = null;
		LinkedList<Edge> linkedList = null;
		Edge edge = null;
		for (int i = 0; i < particlesFrame1.size(); i++) {
			particle1 = particlesFrame1.get(i);
			linkedList =  new LinkedList<Edge>();
			orderedLists.add(new LinkedList<Edge>());
			for (int j = 0; j < particlesFrame2.size(); j++) {
				particle2 = particlesFrame2.get(j);
				edge = new Edge(particle1, particle2);
				this.particlesMatrix[i][j] = edge;
//				if(edge.getDistance() > linkedList)
			}
		}
	}
	
//	public List<Particle> getNClosest(int index) {
//		
//	}

}
