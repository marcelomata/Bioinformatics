package trackingSTP;


import trackingInterface.Object4D;
import trackingInterface.TrackingAction;
import trackingInterface.TrackingStrategy;
import trackingSTP.impl.AssociationMinDistance;
import trackingSTP.impl.EventSeekerExample;
import trackingSTP.impl.HandlerExemple;
import trackingSTP.impl.Object3DTracking;

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
		for (int i = 1; i <= inObject.getSize(); i++) {
			object3DToAssociate = new Object3DTracking(inObject4D.getLastFrame(), inObject4D.getFrame());
			current = nextAction();
			current.execute(object3DToAssociate);
		}
	}

}
