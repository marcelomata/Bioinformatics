package trackingSPT.segmentation;

import ij.ImagePlus;
import mcib3d.image3d.ImageInt;
import mcib3d.image3d.processing.BinaryMorpho;
import mcib3d.utils.ThreadUtil;
import trackingSPT.objects3D.TrackingContextSPT;

public class MathMorphologyAction implements SegmentationAction {

	public MathMorphologyAction(TrackingContextSPT context) {
		
	}

	@Override
	public void execute() {
		float radXY = 1;
        float radZ = 0;
		ImagePlus plus = null;
		ImageInt input = ImageInt.wrap(plus);
		BinaryMorpho.binaryErode(input, radXY, radZ, ThreadUtil.getNbCpus());	
	}

}
