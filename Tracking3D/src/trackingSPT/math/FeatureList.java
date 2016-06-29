package trackingSPT.math;

import java.util.ArrayList;
import java.util.List;

import trackingSPT.objects3D.ObjectTree3D;

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

	public double getResult(ObjectTree3D obj1, ObjectTree3D obj2, int i) {
		return features.get(i).getResult(obj1, obj2);
	}

}
