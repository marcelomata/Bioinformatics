package trackingSPT.segmentation;

import ij.ImagePlus;
import ij.plugin.Duplicator;
import mcib3d.image3d.IterativeThresholding.TrackThreshold;
import trackingSPT.objects3D.TrackingContextSPT;

public class Segmentation3D extends SegmentationAction {

	public Segmentation3D(TrackingContextSPT context) {
		super(context);
	}

	@Override
	public void execute() {
		ImagePlus myPlus = context.getCurrentSegFrame();
		
		int step = 1;
//        if (myPlus.getBitDepth() == 8) {
//            step = 1;
//        } else {
//            step = 100;
//        }

        // extract current time 
        Duplicator dup = new Duplicator();
        int[] dim = myPlus.getDimensions();
        int selectedTime = myPlus.getFrame();
        ImagePlus timedup = dup.run(myPlus, 1, 1, 1, dim[3], selectedTime, selectedTime);
        //timedup.show("Frame_" + selectedTime);
        int thmin = (int) THRESHOLD;
        // is starts at mean selected, use mean, maybe remove in new version
//        thmin = (int) ImageHandler.wrap(timedup).getMean();

        TrackThreshold TT = new TrackThreshold((int)MIN_SIZE, (int)MAX_SIZE, step, step, thmin);
        // 8-bits switch to step method
        int tmethod = TrackThreshold.THRESHOLD_METHOD_STEP;
        TT.setMethodThreshold(tmethod);
        int cri = TrackThreshold.CRITERIA_METHOD_MSER;
        TT.setCriteriaMethod(cri);
        ImagePlus res = TT.segment(timedup, true);
        saveImage(res, context.getSegFileName());
	}

}