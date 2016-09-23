package trackingSPT;

import ij.IJ;
import ij.ImagePlus;

import java.io.File;
import java.util.List;

import mcib3d.image3d.ImageHandler;
import mcib3d.image3d.ImageInt;
import mcib3d.image3d.ImageShort;
import mcib3d.image3d.IterativeThresholding.TrackThreshold;
import trackingPlugin.Log;
import trackingSPT.events.enums.EventType;
import trackingSPT.objects3D.MissedObject;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.SegmentationError;
import trackingSPT.objects3D.TrackingContextSPT;

public class FeedbackSegmentation extends TrackingAction {
	
	private ImageInt markersMissing;
	private ImageInt zonesMissing;
//	private ImageInt markersMerging;
//	private ImageInt zonesMerging;
//	private ImageInt markersSplitting;
//	private ImageInt zonesSplitting;

	public FeedbackSegmentation(TrackingContextSPT context) {
		super(context);
		markersMissing = null;
		zonesMissing = null;
//		markersMerging = null;
//		zonesMerging = null;
//		markersSplitting = null;
//		zonesSplitting = null;
	}
	
	@Override
	public void execute() {
		List<SegmentationError> misssings = this.context.getSegmentationError(EventType.MISSING);
//		List<SegmentationError> mergings = this.context.getSegmentationError(EventType.MERGING);
//		List<SegmentationError> splittings = this.context.getSegmentationError(EventType.SPLITTING);
		
		Log.println("Feedback to segmentation algorithm of segmentation errors (missing, merging and splitting)");
		
		handleMissingSegErrors(misssings);	
//		handleMergingSegErrors(mergings, markersMerging, zonesMerging);	
//		handleSplittingSegErrors(splittings, markersSplitting, zonesSplitting);
	}

	private void handleMissingSegErrors(List<SegmentationError> missings) {
		ObjectTree3D obj;
		
		for (SegmentationError segError : missings) {
			obj = ((MissedObject)segError).getObject();
			setMarkersZones(markersMissing, zonesMissing, segError);
			markersMissing.draw(obj.getObject(), obj.getObject().getValue());
		}
		applySegmentationParameters(markersMissing, zonesMissing);
		
	}
	
//	private void handleMergingSegErrors(List<SegmentationError> mergings) {
//		List<ObjectTree3D> objs;
//		for (SegmentationError segError : mergings) {
//			objs = ((MergedObject)segError).getSourceObjects();
//			setMarkersZones(markersMerging, zonesMerging, segError);
//			drawObjectList(markersMerging, objs);
//		}
//		applySegmentationParameters(markersMerging, zonesMerging);
//	}
//	
//	private void handleSplittingSegErrors(List<SegmentationError> splittings) {
//		ObjectTree3D obj;
//		for (SegmentationError segError : splittings) {
//			obj = ((SplittedObject)segError).getObjectSource();
//			setMarkersZones(markersSplitting, zonesSplitting, segError);
//			markersSplitting.draw(obj.getObject(), obj.getObject().getValue());
//		}
//		applySegmentationParameters(markersSplitting, zonesSplitting);
//	}
	
	private void drawObjectList(ImageInt markers, List<ObjectTree3D> objs) {
		for (ObjectTree3D obj : objs) {
			markers.draw(obj.getObject(), obj.getObject().getValue());
		}
	}

	private void setMarkersZones(ImageInt markers, ImageInt zones, SegmentationError segError) {
		markers = new ImageShort(this.context.getImagePlus(segError.getFrameError()));
//		markers.erase();
		zones = this.context.getZones(segError.getFrameError());
	}
	
	public void applySegmentationParameters(ImageInt markers, ImageInt zones) {
		ImagePlus myPlus = context.getImagePlus(context.getFrameTime());
		
		int step = 1;

		int thmin = 30;
        // is starts at mean selected, use mean, maybe remove in new version
        thmin = (int) ImageHandler.wrap(myPlus).getMean();

        TrackThreshold TT = new TrackThreshold((int)0, (int)10000, step, step, thmin);
        // 8-bits switch to step method
        int tmethod = TrackThreshold.THRESHOLD_METHOD_STEP;
        TT.setMethodThreshold(tmethod);
        int cri = TrackThreshold.CRITERIA_METHOD_MSER;
        TT.setCriteriaMethod(cri);
        TT.setImageMarkers(markers);
        TT.setImageZones(null);
        ImagePlus res = TT.segment(myPlus, true);
        saveImage(res, context.getSegmentedCorrectedDataDir());
	}
	
	private void saveImage(ImagePlus imagePlus, File segmentedDataDir) {
		IJ.saveAsTiff(imagePlus, segmentedDataDir + "/" + context.getSegFileName() + IJ.pad(context.getCurrentFrame() + 0, 2) + ".tif");
	}

}
