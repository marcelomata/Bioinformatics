package trackingSPT.objects;

import trackingInterface.ObjectAction;

public interface MovieObjectAction {
	
	ObjectAction getFrame();
	
	void nextFrame();
	
	int getFrameTime();
	
	int getSize();

}
