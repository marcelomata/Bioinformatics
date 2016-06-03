package trackingSPT.objects;

import java.util.List;

public abstract class SplittingMergingObj extends EventSeekerObj {
	
	public abstract List<TemporalObject> getLeftTargetObjects();
	
	public abstract List<TemporalObject> getLeftSourceObjects();
	
}