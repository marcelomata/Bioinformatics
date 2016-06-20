package trackingSPT.objects3D;

import java.util.ArrayList;
import java.util.List;

import ij.ImagePlus;
import ij.plugin.Duplicator;
import mcib3d.geom.Objects3DPopulation;
import trackingSPT.mcib3DObjects.Objects3DPopulationSPT;

public class ObjectActionSPT4D implements MovieObjectAction {
	
	private List<Objects3DPopulationSPT> population3DPlusT;
	private int currentFrame;
	
	/**
	 * 
	 * @param imp It has to be a 4D image (3D+t)
	 */
	public ObjectActionSPT4D(ImagePlus imp) {
		this.population3DPlusT = new ArrayList<Objects3DPopulationSPT>();
		this.currentFrame = 0;
		loadFrames3D(imp);
	}

	private void loadFrames3D(ImagePlus imp) {
		// extract each time 
        Duplicator dup = new Duplicator();
        int[] dim = imp.getDimensions();
        ImagePlus timedup;
        Objects3DPopulationSPT populationT;
        for (int t = 0; t <= imp.getNFrames(); t++) {
        	timedup = dup.run(imp, 1, 1, 1, dim[3], t, t);
        	populationT = new Objects3DPopulationSPT(new Objects3DPopulation(timedup));
        	population3DPlusT.add(populationT);
		}
	}

	public Objects3DPopulationSPT getLastFrame() {
		if(currentFrame == 0) {
			return null;
		}
		return population3DPlusT.get(currentFrame-1);
	}

	@Override
	public Objects3DPopulationSPT getFrame() {
		return population3DPlusT.get(currentFrame);
	}
	
	@Override
	public void nextFrame() {
		currentFrame++;
	}

	public int getSize() {
		return population3DPlusT.size();
	}
	
	public int getFrameTime() {
		return currentFrame;
	}
	
	public TemporalPopulation3D getAssociationObjectAction() {
		return new Object3DTracking(this.getLastFrame(), this.getFrame());
	}
	
	public TemporalPopulation3D getTemporalPopulation(TrackingResultObjectAction trackingResult) {
		Object3DTracking objectTracking = new Object3DTracking(this.getLastFrame(), this.getFrame());
		return objectTracking;
	}
}
