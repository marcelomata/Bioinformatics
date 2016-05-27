package trackingSTP;

import java.util.ArrayList;
import java.util.List;

import ij.ImagePlus;
import ij.plugin.Duplicator;
import mcib3d.geom.Objects3DPopulation;
import trackingInterface.Object4D;

public class ObjectAction4D implements Object4D {
	
	private List<Objects3DPopulationAdapter> population3DPlusT;
	private int currentFrame;
	
	/**
	 * 
	 * @param imp It has to be a 4D image (3D+t)
	 */
	public ObjectAction4D(ImagePlus imp) {
		this.population3DPlusT = new ArrayList<Objects3DPopulationAdapter>();
		this.currentFrame = 0;
		loadFrames3D(imp);
	}

	private void loadFrames3D(ImagePlus imp) {
		// extract each time 
        Duplicator dup = new Duplicator();
        int[] dim = imp.getDimensions();
        ImagePlus timedup = null;
        Objects3DPopulationAdapter populationT = null;
        for (int t = 1; t <= imp.getNFrames(); t++) {
        	timedup = dup.run(imp, 1, 1, 1, dim[3], t, t);
        	populationT = new Objects3DPopulationAdapter(new Objects3DPopulation(timedup));
        	population3DPlusT.add(populationT);
		}
	}

	@Override
	public Objects3DPopulationAdapter getLastFrame() {
		if(currentFrame == 0) {
			return null;
		}
		return population3DPlusT.get(currentFrame);
	}

	@Override
	public Objects3DPopulationAdapter getFrame() {
		return null;
	}
	
	@Override
	public void nextFrame() {
		currentFrame++;
	}

	@Override
	public int getSize() {
		return population3DPlusT.size();
	}

}
