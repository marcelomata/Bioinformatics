package trackingSPT.objects3D;

import java.util.List;
import java.util.Map;

import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.geom.Point3D;
import trackingSPT.MotionField;
import trackingSPT.mcib3DObjects.Objects3DPopulationSPT;

public class TrackingResult3DSPT extends TrackingResultObjectAction {

	private MotionField motionField;
	private double maxX;
	private double maxY;
	private double maxZ;
	private double minX;
	private double minY;
	private double minZ;
	
	public TrackingResult3DSPT(ObjectActionSPT4D inObject) {
		super(inObject);
		this.maxX = -9999;
		this.maxY = -9999;
		this.maxZ = -9999;
		this.minX = 9999;
		this.minY = 9999;
		this.minZ = 9999;
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
		Object3D obj;
		for (int i = 0; i < population.getNbObjects(); i++) {
			obj = population.getObject(i);
			this.motionField.addNewObject(new ObjectTree3D(obj, getCurrentFrame()));
			if(obj != null) {
				updateBoundBox(obj.getCenterAsPoint());
			}
		}
		
		this.objectAction.nextFrame();
	}

	private void updateBoundBox(Point3D p) {
		maxX = p.getX() > maxX ? p.getX() : maxX;
		maxY = p.getY() > maxY ? p.getY() : maxY;
		maxZ = p.getZ() > maxZ ? p.getZ() : maxZ;
		minX = p.getX() < minX ? p.getX() : minX;
		minY = p.getY() < minY ? p.getY() : minY;
		minZ = p.getZ() < minZ ? p.getZ() : minZ;	 
	}

	public void addNewObjectId(Integer id, ObjectTree3D objectTarget) {
		motionField.addNewObjectId(id, objectTarget);
		if(objectTarget.getObject() != null) {
			updateBoundBox(objectTarget.getObject().getCenterAsPoint());
		}
	}

	public void finishObjectTracking(ObjectTree3D obj1) {
		motionField.finishObject(obj1);
	}

	public void addNewObject(ObjectTree3D obj) {
		motionField.addNewObject(obj);
		if(obj.getObject() != null) {
			updateBoundBox(obj.getObject().getCenterAsPoint());
		}
	}

//	public void fillFinishedTrackings() {
//		motionField.addVoidObjectFinishedTrack(getCurrentFrame());
//	}

	@Override
	public Point3D getMaxPoint() {
		return new Point3D(maxX, maxY, maxZ);
	}

	@Override
	public Point3D getMinPoint() {
		return new Point3D(minX, minY, minZ);
	}

	public int getNumberOfFrames() {
		return objectAction.getSize();
	}

	/**
	 * 
	 * @return map between the track and the list of ObjectTree3D in this track
	 */
	public Map<Integer, List<ObjectTree3D>> getFinalResultByTrack() {
		return motionField.getFinalResultByTrack();
	}
	
	/**
	 * 
	 * @return list of List<Particle> in each frame
	 */
	public List<List<ObjectTree3D>> getFinalResultByFrame() {
		return motionField.getFinalResultByFrame();
	}
	
}
	