import java.util.List;

public class Track {

	private List<Vector> vectors;

	public Vector getVector(int i) {
		return vectors.get(i);
	}

	public int size() {
		return vectors.size();
	}

	public List<Vector> getVectorList() {
		return vectors;
	}
	
}
