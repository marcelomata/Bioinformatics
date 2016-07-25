package trackingSPT.segmentation;

import ij.ImagePlus;
import ij.plugin.filter.RankFilters;
import trackingSPT.objects3D.TrackingContextSPT;

public class FilteringAction extends SegmentationAction {

	public FilteringAction(TrackingContextSPT context) {
		super(context);
	}

	@Override
	public void execute() {
		ImagePlus imp = context.getCurrentRawFrame();
        RankFilters filter = new RankFilters();
		filter.rank(imp.getProcessor(), RADIUS, RankFilters.MEDIAN, RankFilters.DARK_OUTLIERS, (float)THRESHOLD);
		ImagePlus res = new ImagePlus("Res", imp.getStack());
        // Save the image under the specified folder
		saveImage(res, context.getSegFileName());
	}
}

