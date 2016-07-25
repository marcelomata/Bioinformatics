package trackingSPT.objects3D;

import ij.ImagePlus;
import trackingInterface.Frame;

public interface MovieObjectAction {
	
	Frame getFrame();
	
	ImagePlus getRawFrame();
	
	ImagePlus getSegFrame();
	
	void nextFrame();
	
	int getFrameTime();
	
	int getSize();
	
	TemporalPopulation3D getTemporalPopulation3D();

	String getSegFileName();

}
