package trackingSPT.objects.events;

import java.util.List;
import java.util.Map;

import trackingSPT.objects.ObjectTree;

public abstract class SplittingMergingObj extends EventSeekerObj {
	
	public abstract List<ObjectTree> getLeftTargetObjects();
	
	public abstract List<ObjectTree> getLeftSourceObjects();
	
	public abstract Map<ObjectTree, List<ObjectTree>> getAssociationsMap();
	
	public abstract List<ObjectTree> getAssociationsMapSources();
	
}