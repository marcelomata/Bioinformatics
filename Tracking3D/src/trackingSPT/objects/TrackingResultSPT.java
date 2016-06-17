package trackingSPT.objects;

import java.util.ArrayList;
import java.util.List;

import mcib3d.geom.Objects3DPopulation;
import trackingSPT.MotionField;
import trackingSPT.mcib3DAdapters.Objects3DPopulationAdapter;

public class TrackingResultSPT extends TrackingResultObjectAction {

	private MotionField motionField;
	private List<MissedObject> misses;
	
	public TrackingResultSPT(ObjectActionSPT4D inObject) {
		super(inObject);
		this.motionField = new MotionField();
		this.misses = new ArrayList<MissedObject>();
	}

	public MotionField getMotionField() {
		return motionField;
	}
	
	public List<ObjectTree> getListLastObjects() {
		return motionField.getListLastObjects();
	}
	
	public List<MissedObject> getMisses() {
		return misses;
	}
	
	@Override
	public void init() {
		Objects3DPopulationAdapter populationAdapter = (Objects3DPopulationAdapter) this.objectAction.getFrame();
		Objects3DPopulation population = populationAdapter.getObject3D();
		for (int i = 0; i < population.getNbObjects(); i++) {
			this.motionField.addNewObject(new ObjectTree(population.getObject(i)));
		}
		
		this.objectAction.nextFrame();
	}

	public void addNewObjectId(Integer id, ObjectTree objectTarget) {
		motionField.addNewObjectId(id, objectTarget);
	}

	public void finishObjectTracking(Integer id) {
		motionField.finishObject(id);
	}

	public void addNewObject(ObjectTree obj) {
		motionField.addNewObject(obj);
	}

	public void addMissed(ObjectTree objMissed) {
		misses.add(new MissedObject(objMissed, this.objectAction.getFrameTime()));
	}

	public void fillFinishedTrackings() {
		motionField.addVoidObjectFinishedTrack();
	}
	
}
	