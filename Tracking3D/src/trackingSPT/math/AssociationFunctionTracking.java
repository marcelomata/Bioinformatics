package trackingSPT.math;

import trackingSPT.events.enums.FeatureType;
import trackingSPT.objects3D.ObjectTree3D;

public class AssociationFunctionTracking {
	
	private FeatureList featureList;
	
	public AssociationFunctionTracking() {
		this.featureList = new FeatureList();
		this.featureList.addFeature(new Feature(new FunctionEuclidianDistance(), FeatureType.EUCLIDIAN_DISTANCE, 1));
		this.featureList.addFeature(new Feature(new FunctionColocalization(), FeatureType.COLOCALIZATION, 0.05));
		this.featureList.addFeature(new Feature(new FunctionArea(), FeatureType.AREA, 1));
	}
	
	public double calcDistance(ObjectTree3D obj1, ObjectTree3D obj2) {
		double distance = 0;
		double []valuesDim = new double[featureList.size()];
		for (int i = 0; i < featureList.size(); i++) {
			valuesDim[i] = featureList.getResult(obj1, obj2, i);
			distance += valuesDim[i]*valuesDim[i];
		}
		
		return Math.sqrt(distance);
	}
}
