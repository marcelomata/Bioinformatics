package trackingSPT.events.eventfinder.mergingsplitting;

import java.util.List;
import java.util.Map;

import trackingInterface.ObjectAction;
import trackingSPT.objects3D.ObjectTree3D;

public interface SplittingMergingObj extends ObjectAction {
	
	public abstract List<ObjectTree3D> getLeftTargetObjects();
	
	public abstract List<ObjectTree3D> getLeftSourceObjects();
	
	public abstract Map<ObjectTree3D, List<ObjectTree3D>> getAssociationsMap();
	
	public abstract List<ObjectTree3D> getAssociationsMapSources();
	
}