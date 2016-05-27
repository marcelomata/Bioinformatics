package trackingInterface;

public interface Object4D extends ObjectAction {
	
	ObjectAction getLastFrame();
	
	ObjectAction getFrame();
	
	void nextFrame();
	
	int getSize();

}
