package trackingInterface;

import trackingSPT.objects3D.TrackingResultObjectAction;

public interface TrackingContext {

	void generateSegmentationErrorsFile();
	
	int getFrameTime();

	void clear();

	int getSize();

	void nextFrame();

	TrackingResultObjectAction getResult();

	void updateObjectsAttributes();
	
}
