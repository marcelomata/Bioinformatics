package trackingSPT.math;

import trackingSPT.objects3D.ObjectTree3D;

public class FunctionSplittingMergingColocalization implements FunctionCalcObjectRelation {

	@Override
	public double calculate(ObjectTree3D obj1, ObjectTree3D obj2) {
        double vol1 = obj1.getObject().getVolumePixels();
        double vol2 = obj2.getObject().getVolumePixels();
        double coloc = obj1.getObject().getColoc(obj2.getObject());
        double threshold = 0.8;
        
        double norm = (vol1 + vol2 - coloc) / (vol1 + vol2);
        double relationSize = vol1 / (vol1 + vol2);

        if(norm == 1) {
        	return 1000;
        } else if((relationSize < 0.8 && relationSize > (1 - threshold)) && norm > 0.9) {
        	return 1000;
        }
        
		return norm;
	}

}
