package trackingSPT.math;

import trackingSPT.events.enums.FeatureType;
import trackingSPT.objects3D.ObjectTree3D;

public class AssociationFunctionTracking {
	
	private FeatureList featureList;
	
	public AssociationFunctionTracking(FeatureList featureList) {
		this.featureList = featureList;
		this.featureList.addFeature(new Feature(new FunctionEuclidianDistance(), FeatureType.EUCLIDIAN_DISTANCE, 0.34));
		this.featureList.addFeature(new Feature(new FunctionColocalization(), FeatureType.COLOCALIZATION, 0.33));
		this.featureList.addFeature(new Feature(new FunctionBorderDistance(), FeatureType.BORDER_DISTANCE, 0.33));
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
