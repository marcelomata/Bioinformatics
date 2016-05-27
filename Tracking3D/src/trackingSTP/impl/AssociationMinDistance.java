package trackingSTP.impl;


import trackingInterface.ObjectAction;
import trackingSTP.Association;

public class AssociationMinDistance extends Association {

	@Override
	public ObjectAction execute(ObjectAction object) {
		return new AssociatedObjectList();
	}


}
