package trackingSPT.math;

import java.util.ArrayList;
import java.util.List;

public class FeatureList {
	
	private List<Feature> features;
	
	
	public FeatureList() {
		this.features = new ArrayList<Feature>();
	}
	
	public void addFeature(Feature feature) {
		this.features.add(feature);
	}
	
	public int size() {
		return features.size();
	}

}
