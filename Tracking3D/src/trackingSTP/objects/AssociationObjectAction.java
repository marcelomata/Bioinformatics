package trackingSTP.objects;

import trackingInterface.ObjectAction;
import trackingSTP.mcib3DAdapters.Objects3DPopulationAdapter;

public abstract class AssociationObjectAction implements ObjectAction {
	
	public abstract Objects3DPopulationAdapter getObjectT();
	
	public abstract Objects3DPopulationAdapter getObjectTPlus1();
	
}
