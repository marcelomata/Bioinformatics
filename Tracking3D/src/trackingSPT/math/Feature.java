package trackingSPT.math;

import trackingSPT.events.enums.FeatureType;
import trackingSPT.objects3D.ObjectTree3D;

public class Feature {

	private FunctionCalcObjectRelation function;
	private FeatureType type;
	private double weight;
	
	public Feature(FunctionCalcObjectRelation function, FeatureType type, double weight) {
		this.function = function;
		this.type = type;
		this.weight = weight;
	}
	
	public double getResult(ObjectTree3D obj1, ObjectTree3D obj2) {
		return function.calculate(obj1, obj2)*weight;
	}
	
	public FeatureType getType() {
		return type;
	}
	
}
