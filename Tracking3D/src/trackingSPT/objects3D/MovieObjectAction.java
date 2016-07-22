package trackingSPT.objects3D;

import trackingInterface.Frame;

public interface MovieObjectAction {
	
	Frame getFrame();
	
	void nextFrame();
	
	int getFrameTime();
	
	int getSize();

}
