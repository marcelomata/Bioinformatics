package trackingSPT.objects3D;

import trackingInterface.ObjectAction;
import trackingSPT.mcib3DObjects.Objects3DPopulationSPT;

public interface TemporalPopulation3D extends ObjectAction {
	
	public abstract Objects3DPopulationSPT getObjectT();
	
	public abstract Objects3DPopulationSPT getObjectTPlus1();
	
}
