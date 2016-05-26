

import trackingPlugin.Associate;
import trackingPlugin.AssociatedObjectList;
import trackingPlugin.ObjectAction;

public class AssociationMinDistance extends Associate {

	@Override
	public ObjectAction execute(ObjectAction object) {
		return new AssociatedObjectList();
	}


}
