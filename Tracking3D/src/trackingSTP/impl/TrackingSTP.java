package trackingSTP.impl;


import trackingInterface.Object4D;
import trackingInterface.TrackingAction;
import trackingInterface.TrackingStrategy;

public class TrackingSTP extends TrackingStrategy {
	
	public TrackingSTP(Object4D inObject) {
		super(inObject);
	}

	@Override
	public void build() {
		addAction(new AssociationMinDistance());
		addAction(new EventSeekerExample());
		addAction(new HandlerExemple());
	}

	@Override
	public void run() {
		TrackingAction current = null;
		Object3DTracking object3DToAssociate = null;
		ObjectAction4D inObject4D = (ObjectAction4D)inObject;
		inObject4D.nextFrame();
		current = nextAction();
		//To first test, only from the frame 6 to frame 12
		while(inObject4D.getCurrentFrame() < inObject4D.getSize()) {
//			if()
			object3DToAssociate = new Object3DTracking(inObject4D.getLastFrame(), inObject4D.getFrame());
			current.execute(object3DToAssociate);
//			current = nextAction();
			inObject4D.nextFrame();
		}
	}

}
