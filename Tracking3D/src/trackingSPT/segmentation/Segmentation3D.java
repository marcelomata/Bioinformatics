package trackingSPT.segmentation;

import ij.ImagePlus;
import ij.measure.Calibration;
import mcib3d.image3d.ImageInt;
import mcib3d.image3d.ImageLabeller;
import trackingSPT.objects3D.TrackingContextSPT;

public class Segmentation3D extends SegmentationAction {

	public Segmentation3D(TrackingContextSPT context) {
		super(context);
	}

	@Override
	public void execute() {
		ImagePlus myPlus = context.getCurrentSegFrame();
		
		ImageLabeller labeler = new ImageLabeller();
        labeler.setMinSize((int) MIN_SIZE);
        labeler.setMaxsize((int) MAX_SIZE);
        Calibration cal = myPlus.getCalibration();
        ImageInt img = ImageInt.wrap(myPlus);
        ImageInt bin = img.thresholdAboveInclusive((float) THRASHOLD);
        if (cal != null) {
            bin.setCalibration(cal);
        }
        ImageInt res = labeler.getLabels(bin);
        if (cal != null) {
            res.setCalibration(cal);
        }
        
        saveImage(res.getImagePlus(), context.getSegFileName());
	}

}