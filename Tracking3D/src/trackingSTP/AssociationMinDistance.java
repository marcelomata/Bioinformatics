package trackingSTP;


import trackingPlugin.Association;
import trackingPlugin.AssociatedObjectList;
import trackingPlugin.ObjectAction;

public class AssociationMinDistance extends Association {

	@Override
	public ObjectAction execute(ObjectAction object) {
		return new AssociatedObjectList();
	}


}
