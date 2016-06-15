package trackingInterface;

public interface ObjectAction4D extends ObjectAction {
	
	ObjectAction getLastFrame();
	
	ObjectAction getFrame();
	
	int getFrameTime();
	
	void nextFrame();
	
	int getSize();

}
