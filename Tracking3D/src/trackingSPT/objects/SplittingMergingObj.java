package trackingSPT.objects;

import java.util.List;
import java.util.Map;

public abstract class SplittingMergingObj extends EventSeekerObj {
	
	public abstract List<TemporalObject> getLeftTargetObjects();
	
	public abstract List<TemporalObject> getLeftSourceObjects();
	
	public abstract Map<TemporalObject, List<TemporalObject>> getAssociationsMap();
	
}