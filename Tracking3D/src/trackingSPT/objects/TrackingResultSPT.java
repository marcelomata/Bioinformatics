package trackingSPT.objects;

import java.util.ArrayList;
import java.util.List;

import mcib3d.geom.Objects3DPopulation;
import trackingInterface.ObjectAction;
import trackingSPT.MotionField;

public class TrackingResultSPT extends TrackingResultObjectAction {

	private MotionField motionField;
	private List<MissedObject> misses;
	
	public TrackingResultSPT() {
		this.motionField = new MotionField();
		this.misses = new ArrayList<MissedObject>();
	}

	public MotionField getMotionField() {
		return motionField;
	}
	
	public List<TemporalObject> getListLastObjects() {
		return motionField.getListLastObjects();
	}
	
	public List<MissedObject> getMisses() {
		return misses;
	}
	
	@Override
	public void init(ObjectAction objectAction) {
		ObjectActionSPT4D inObject4D = (ObjectActionSPT4D) objectAction;
		Objects3DPopulation populationAdapter = inObject4D.getFrame().getObject3D();
		for (int i = 0; i < populationAdapter.getNbObjects(); i++) {
			this.motionField.addNewObject(new TemporalObject(populationAdapter.getObject(i)));
		}
		
		inObject4D.nextFrame();
	}

	public void addNewObjectId(Integer id, TemporalObject objectTarget) {
		motionField.addNewObjectId(id, objectTarget);
	}

	public void finishObjectTracking(Integer id) {
		motionField.finishObject(id);
	}

	public void addNewObject(TemporalObject obj) {
		motionField.addNewObject(obj);
	}

	public void addMissed(TemporalObject objMissed) {
		misses.add(new MissedObject(objMissed, motionField.getMapObjects().get(objMissed.getId()).size()));
	}
	
}
	