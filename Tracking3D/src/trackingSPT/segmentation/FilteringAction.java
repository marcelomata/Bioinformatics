package trackingSPT.segmentation;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.RankFilters;
import trackingPlugin.ImageJStatic;
import trackingSPT.objects3D.TrackingContextSPT;

public class FilteringAction implements SegmentationAction {
	
	private static final double RADIUS = 3;
	private static final double THRESHOLD = 50;
	private static final String FILE_NAME = "mask";
	
	private File rawDataDir;
	private File segmentedDataDir;

	public FilteringAction(TrackingContextSPT context) {
		this.rawDataDir = context.getRawDataDir();
		this.segmentedDataDir = context.getSegmentedDataDir();
	}

	@Override
	public void execute() {
        File []frames = rawDataDir.listFiles();
        List<File> framesList = Arrays.asList(frames);
        Collections.sort(framesList);
        frames = framesList.toArray(new File[framesList.size()]);
        
        ImagePlus ip;
        ImagePlus res;
        RankFilters filter = new RankFilters();
        int t = 0;
        for (int i = 0; i < frames.length; i++) {
			if(frames[i].getName().contains(".tif")) {
				ip =  ImageJStatic.getImageRaw(rawDataDir, frames[i].getName(), t).getImagePlus();
				filter.rank(ip.getProcessor(), RADIUS, RankFilters.MEDIAN, RankFilters.DARK_OUTLIERS, (float)THRESHOLD);
				
				res = new ImagePlus("Res", ip.getStack());
	
	            // Save the image under the specified folder
	            IJ.saveAsTiff(res, segmentedDataDir.getAbsolutePath() + "/" + FILE_NAME + IJ.pad(i + 0, 2) + ".tif");
				
				t++;
			}
		}
        
		
		
		 
	}
}

