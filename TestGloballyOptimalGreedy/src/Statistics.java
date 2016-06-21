import java.util.List;

public class Statistics {

	public double markovModel(List<Track> tracks) {
		double p = 1; 
		for (Track track : tracks) {
			p *= trackProbability(track);
		}
		
		return p;
	}
	
	public double trackProbability(Track track) {
		double ps = probabilityStart(track.getVector(0));
		double pt = probabilityStart(track.getVector(track.size()-1));
		double prior = 1;
		List<Vector> vectors = track.getVectorList();
		for (int i = 1; i < vectors.size()-2; i++) {
			prior *= dynamicModel(vectors.get(i+1), vectors.get(i)); 
		}
		return ps*prior*pt;
	}
	
	private double dynamicModel(Vector vector1, Vector vector2) {
		return 0;
	}

	public double probabilityStart(Vector x) {
		return 0;
	}
	
	public double probabilityTermination(Vector x) {
		return 0;
	}
	
	public double likelihood(Track track) {
		double p = 1; 
		for (Track track : tracks) {
			p *= trackProbability(track);
		}
	}
	
	public double foregroundLikelihood(Feature feature) {
		
		return 0;
	}
	
}
