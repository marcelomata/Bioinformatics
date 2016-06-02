package trackingSPT.objects;

import trackingSPT.mcib3DAdapters.Objects3DPopulationAdapter;

public abstract class AssociationObjectAction extends EventSeekerObj {
	
	public abstract Objects3DPopulationAdapter getObjectT();
	
	public abstract Objects3DPopulationAdapter getObjectTPlus1();
	
}
