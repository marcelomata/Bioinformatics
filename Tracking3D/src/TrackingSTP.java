

import trackingPlugin.ObjectAction;
import trackingPlugin.TrackingStrategy;

public class TrackingSTP extends TrackingStrategy {
	
	public TrackingSTP(ObjectAction inObjects) {
		super(inObjects);
	}

	@Override
	public void build() {
		addAction(new AssociationMinDistance());
		addAction(new EventSeekerExample());
		addAction(new HandlerExemple());
	}

}
