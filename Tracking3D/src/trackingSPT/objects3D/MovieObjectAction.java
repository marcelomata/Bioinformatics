package trackingSPT.objects3D;

import trackingInterface.ObjectAction;

public interface MovieObjectAction {
	
	ObjectAction getFrame();
	
	void nextFrame();
	
	int getFrameTime();
	
	int getSize();

}
