package trackingSPT.math;

import trackingSPT.objects3D.ObjectTree3D;

public class FunctionBorderDistance implements FunctionCalcObjectRelation {

	@Override
	public double calculate(ObjectTree3D obj1, ObjectTree3D obj2) {
//		return obj1.getObject().distBorderPixel(obj2.getObject());
		return 0;
	}

}
