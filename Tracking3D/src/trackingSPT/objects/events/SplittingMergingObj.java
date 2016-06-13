package trackingSPT.objects.events;

import java.util.List;
import java.util.Map;

import trackingSPT.objects.TemporalObject;

public abstract class SplittingMergingObj extends EventSeekerObj {
	
	public abstract List<TemporalObject> getLeftTargetObjects();
	
	public abstract List<TemporalObject> getLeftSourceObjects();
	
	public abstract Map<TemporalObject, List<TemporalObject>> getAssociationsMap();
	
	public abstract List<TemporalObject> getAssociationsMapSources();
	
}