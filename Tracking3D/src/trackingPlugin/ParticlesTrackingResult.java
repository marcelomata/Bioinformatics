package trackingPlugin;

import java.util.List;
import java.util.Map;

import cell3DRenderer.ParticlesObjects;
import mcib3d.geom.Object3D;
import trackingSPT.objects.TrackingResultSPT;

public class ParticlesTrackingResult implements ParticlesObjects {
	
	private TrackingResultSPT trackingResult;
	
	public ParticlesTrackingResult(TrackingResultSPT result) {
		this.trackingResult = result;
	}

	@Override
	public Map<Integer, List<Object3D>> getObjectsListId() {
		return trackingResult.getMotionField().getFinalResult();
	}

}
