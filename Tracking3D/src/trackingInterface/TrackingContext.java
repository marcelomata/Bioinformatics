package trackingInterface;

import java.io.File;

import trackingSPT.objects3D.TrackingResultObjectAction;

public interface TrackingContext {

	void generateSegmentationErrorsFile();
	
	void generateTrackingAnalysisFiles();
	
	int getFrameTime();

	void clear();

	int getSize();

	void nextFrame();

	TrackingResultObjectAction getResult();

	void updateObjectsAttributes();
	
	File getSegmentedCorrectedDataDir();
	
	void setSegmentedCorrectedDataDir(File segCorrDir);
	
}
