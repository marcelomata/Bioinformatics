package trackingSPT.math;

import trackingSPT.events.enums.FeatureType;

public class Feature {

	private FeatureType type;
	private double weight;
	
	public Feature(FeatureType type, double weight) {
		this.type = type;
		this.weight = weight;
	}
	
	
}
