package trackingSPT.math;

import trackingSPT.objects3D.ObjectTree3D;

public class FunctionColocalization implements FunctionCalcObjectRelation {

	@Override
	public double calculate(ObjectTree3D obj1, ObjectTree3D obj2) {
        double vol1 = obj1.getObject().getVolumePixels();
        double vol2 = obj2.getObject().getVolumePixels();
        double coloc = obj1.getObject().getColoc(obj2.getObject());
        
        double norm = (vol1 + vol2 - coloc) / (vol1 + vol2);

        if(norm == 1) {
        	return 100;
        }
		return (1-norm)*100;
	}

}
