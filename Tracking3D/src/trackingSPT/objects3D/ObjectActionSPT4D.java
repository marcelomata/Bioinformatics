package trackingSPT.objects3D;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.Duplicator;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.image3d.ImageHandler;
import trackingSPT.mcib3DObjects.Objects3DPopulationSPT;

public class ObjectActionSPT4D implements MovieObjectAction {
	
	private List<Objects3DPopulationSPT> population3DPlusT;
	private boolean frameByFrame;
	private boolean readFromDirectory;
	private File directory;
	private ImagePlus file;
	private int numberOfSlices; 
	private int currentFrame;
	
	/**
	 * 
	 * @param imp It has to be a 4D image (3D+t)
	 */
	public ObjectActionSPT4D(ImagePlus imp) {
		init();
		this.file = imp;
		loadFrames3D(imp);
	}
	
	/**
	 * 
	 * @param imp It has to be a 4D image (3D+t)
	 */
	public ObjectActionSPT4D(String folder) {
		init();
		this.directory = new File(folder);
		loadFrames3D(folder);
	}
	
	private void init() {
		this.population3DPlusT = new ArrayList<Objects3DPopulationSPT>();
		this.currentFrame = 1;
		this.frameByFrame = false;
		this.readFromDirectory = false;
	}

	private void loadFrames3D(ImagePlus imp) {
		// extract each time 
		this.readFromDirectory = false;
        Duplicator dup = new Duplicator();
        int[] dim = imp.getDimensions();
        ImagePlus timedup;
        Objects3DPopulationSPT populationT;
        try {
	        for (int t = 0; t < imp.getNFrames(); t++) {
	        	timedup = dup.run(imp, 1, 1, 1, dim[3], t, t);
	//        	FileSaver fileSave = new FileSaver(imp);
	//        	fileSave.saveAsTiff("/home/marcelodmo/Documents/data/simulated_15f/simulated_15f-"+(t+1)+".tif");
	        	populationT = new Objects3DPopulationSPT(new Objects3DPopulation(timedup));
	        	population3DPlusT.add(populationT);
			}
        } catch(OutOfMemoryError e) {
        	frameByFrame = true;
        }
	}
	
	private void loadFrames3D(String folder) {
		this.readFromDirectory = true;
        File fileFolder = new File(folder);
        File []frames = fileFolder.listFiles();
        numberOfSlices = frames.length;
        ImagePlus timedup;
        Objects3DPopulationSPT populationT;
        try {
	        for (int t = 0; t < frames.length; t++) {
	            timedup = getImageSeg(t).getImagePlus();
	            populationT = new Objects3DPopulationSPT(new Objects3DPopulation(timedup));
	        	population3DPlusT.add(populationT);
			}
        } catch(OutOfMemoryError e) {
        	frameByFrame = true;
        	population3DPlusT.clear();
        }
	}

	public Objects3DPopulationSPT getLastFrame() {
		if(currentFrame == 1) {
			return null;
		}
		if(!frameByFrame) {
			return population3DPlusT.get(currentFrame-1);
		} else {
	       return getFrameT(currentFrame-1);
		}
	}

	@Override
	public Objects3DPopulationSPT getFrame() {
		if(!frameByFrame) {
			return population3DPlusT.get(currentFrame);
		} else {
	       return getFrameT(currentFrame);
		}
	}
	
	private Objects3DPopulationSPT getFrameT(int t) {
		 Objects3DPopulationSPT populationT;
		 Duplicator dup = new Duplicator();
		 ImagePlus timedup;
		if(readFromDirectory) {
	 		timedup = getImageSeg(t).getImagePlus();
	 		populationT =  new Objects3DPopulationSPT(new Objects3DPopulation(timedup));
		} else {
			int[] dim = file.getDimensions();
	        timedup = dup.run(file, 1, 1, 1, dim[3], t, t);
			populationT =  new Objects3DPopulationSPT(new Objects3DPopulation(timedup));
		}
		return populationT;
	}
	
	public ImageHandler getImageSeg(int t) {
	 	String fileName = directory.getAbsolutePath()+"/"+directory.getName()+"-"+t+".tif";
        ImagePlus plus = IJ.openImage(fileName);
        if (plus == null) {
            System.out.println("No image " + fileName);
            return null;
        }

        return ImageHandler.wrap(plus);
    }
	
	@Override
	public void nextFrame() {
		currentFrame++;
	}

	public int getSize() {
		return numberOfSlices;
	}
	
	public int getFrameTime() {
		return currentFrame;
	}
	
	public TemporalPopulation3D getTemporalPopulation3D() {
		return new Object3DTracking(this.getLastFrame(), this.getFrame());
	}
	
//	public TemporalPopulation3D getTemporalPopulation(TrackingResultObjectAction trackingResult) {
//		Object3DTracking objectTracking = new Object3DTracking(this.getLastFrame(), this.getFrame());
//		return objectTracking;
//	}
}
