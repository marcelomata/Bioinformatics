package trackingSPT.objects;

import trackingSPT.mcib3DAdapters.Objects3DPopulationAdapter;
import trackingSPT.objects.events.EventSeekerObj;

public abstract class TemporalPopulation extends EventSeekerObj {
	
	public abstract Objects3DPopulationAdapter getObjectT();
	
	public abstract Objects3DPopulationAdapter getObjectTPlus1();
	
}
