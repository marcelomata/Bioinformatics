package trackingSPT.segmentation;

import java.io.File;

import ij.IJ;
import ij.ImagePlus;
import trackingInterface.Action;
import trackingSPT.objects3D.TrackingContextSPT;

public abstract class SegmentationAction implements Action {

	protected static final double MIN_SIZE = 1000;
	protected static final double MAX_SIZE = 5000;
	protected static final double THRASHOLD = 30;
	protected static final double RADIUS = 3;
	protected static final double THRESHOLD = 30;
	protected static final String FILE_NAME = "mask";
	
	protected File segmentedDataDir;
	protected TrackingContextSPT context;
	
	public SegmentationAction(TrackingContextSPT context) {
		this.segmentedDataDir = context.getSegmentedDataDir();
		this.context = context;
	}
	
	protected void saveImage(ImagePlus imagePlus, String fileSegName) {
		IJ.saveAsTiff(imagePlus, segmentedDataDir + "/" + fileSegName + IJ.pad(context.getCurrentFrame() + 0, 2) + ".tif");
	}
	
}
