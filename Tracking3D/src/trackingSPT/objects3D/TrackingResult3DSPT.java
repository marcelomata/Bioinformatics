package trackingSPT.objects3D;

import java.util.List;
import java.util.Set;

import mcib3d.geom.Objects3DPopulation;
import trackingSPT.MotionField;
import trackingSPT.mcib3DObjects.Objects3DPopulationSPT;

public class TrackingResult3DSPT extends TrackingResultObjectAction {

	private MotionField motionField;
	
	public TrackingResult3DSPT(ObjectActionSPT4D inObject) {
		super(inObject);
	}

	public MotionField getMotionField() {
		return motionField;
	}
	
	public List<ObjectTree3D> getListLastObjects() {
		return motionField.getListLastObjects();
	}
	
	@Override
	public void init() {
		this.motionField = new MotionField();
		Objects3DPopulationSPT populationAdapter = (Objects3DPopulationSPT) this.objectAction.getFrame();
		Objects3DPopulation population = populationAdapter.getObject3D();
		for (int i = 0; i < population.getNbObjects(); i++) {
			this.motionField.addNewObject(new ObjectTree3D(population.getObject(i)));
		}
		
		this.objectAction.nextFrame();
	}

	public void addNewObjectId(Integer id, ObjectTree3D objectTarget) {
		motionField.addNewObjectId(id, objectTarget);
	}

	public void finishObjectTracking(Integer id) {
		motionField.finishObject(id);
	}

	public void addNewObject(ObjectTree3D obj) {
		motionField.addNewObject(obj);
	}

	public void fillFinishedTrackings() {
		motionField.addVoidObjectFinishedTrack();
		if(motionField.isDifferentNumber()) {
			motionField.printSize();
		}
		System.out.println(motionField.isDifferentNumber());
	}
	
}
	