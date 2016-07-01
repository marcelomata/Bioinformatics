package trackingSPT.math;

import trackingSPT.events.enums.FeatureType;
import trackingSPT.objects3D.ObjectTree3D;

public class Feature {

	private FunctionCalcObjectRelation function;
	private FeatureType type;
	private double attenuation;
	
	public Feature(FunctionCalcObjectRelation function, FeatureType type, double attenuation) {
		this.function = function;
		this.type = type;
		this.attenuation = attenuation;
	}
	
	public double getResult(ObjectTree3D obj1, ObjectTree3D obj2) {
		return function.calculate(obj1, obj2)*attenuation;
	}
	
	public FeatureType getType() {
		return type;
	}
	
}
