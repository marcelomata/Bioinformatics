package trackingSPT.objects3D;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.Duplicator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mcib3d.geom.Objects3DPopulation;
import mcib3d.image3d.ImageInt;
import mcib3d.image3d.regionGrowing.Watershed3DVoronoi;
import trackingInterface.Frame;
import trackingPlugin.ImageJStatic;
import trackingSPT.mcib3DObjects.Objects3DPopulationSPT;

public class ObjectActionSPT4D implements MovieObjectAction {
	
	private List<Objects3DPopulationSPT> population3DPlusT;
	private boolean frameByFrame;
	private boolean readFromDirectory;
	private File directorySeg;
	private File directoryRaw;
	private String fileSegName;
	private ImagePlus file;
	private int numberOfFrames; 
	private int currentFrame;
	private File []rawFramesFile;
	private ImagePlus[] imagePlus;
	private Frame[] frames;
	
	/**
	 * 
	 * @param imp It has to be a 4D image (3D+t)
	 */
	public ObjectActionSPT4D(String folderSeg, String fileSegName, String folderRaw, String fileRawName, int numMaxFrames) {
		init();
		this.fileSegName = fileSegName;
		this.directorySeg = new File(folderSeg);
		this.directoryRaw = new File(folderRaw);
		loadRawFrames3D(folderRaw, numMaxFrames);
	}
	
	private void init() {
		this.population3DPlusT = new ArrayList<Objects3DPopulationSPT>();
		this.currentFrame = 0;
		this.frameByFrame = false;
		this.readFromDirectory = false;
	}

	private void loadRawFrames3D(String folder, int numMaxFrames) {
		this.readFromDirectory = true;
        File fileFolder = new File(folder);
        rawFramesFile = fileFolder.listFiles();
        List<File> framesList = Arrays.asList(rawFramesFile);
        Collections.sort(framesList);
        rawFramesFile = framesList.toArray(new File[framesList.size()]);
        countFrames(numMaxFrames);
        this.imagePlus = new ImagePlus[numberOfFrames]; 
        this.frames = new Frame[numberOfFrames]; 
        frameByFrame = true;
	}

	private void countFrames(int numMaxFrames) {
		for (int i = 0; i < rawFramesFile.length; i++) {
			if(rawFramesFile[i].getName().contains(".tif")) {
				numberOfFrames++;
			}
		}
		if(numberOfFrames > numMaxFrames) {
			numberOfFrames = numMaxFrames;
		}
	}

	public Objects3DPopulationSPT getLastFrame() {
		if(currentFrame == 0) {
			return null;
		}
		if(!frameByFrame) {
			return population3DPlusT.get(currentFrame-1);
		} else {
	       return getFrameT(currentFrame-1);
		}
	}

	@Override
	public Frame getFrame() {
		if(!frameByFrame) {
			return population3DPlusT.get(currentFrame);
		} else {
	       return getFrameT(currentFrame);
		}
	}
	
	@Override
	public ImagePlus getRawFrame() {
	      return getTRawFrame(currentFrame);
	}
	
	private Objects3DPopulationSPT getFrameT(int t) {
		 Objects3DPopulationSPT populationT;
		 Duplicator dup = new Duplicator();
		 ImagePlus timedup;
		if(readFromDirectory) {
	 		timedup = ImageJStatic.getImage(directorySeg, fileSegName+IJ.pad(t, 2)+".tif").getImagePlus();
	 		populationT =  new Objects3DPopulationSPT(new Objects3DPopulation(timedup));
		} else {
			int[] dim = file.getDimensions();
	        timedup = dup.run(file, 1, 1, 1, dim[3], t, t);
			populationT =  new Objects3DPopulationSPT(new Objects3DPopulation(timedup));
		}
		calculateVoronoiRegions(timedup, populationT);
		this.imagePlus[t] = timedup;
		this.frames[t] = populationT;
		return populationT;
	}
	
	public ImagePlus getImagePlus(int frame) {
		return imagePlus[frame];
	}
	
	@Override
	public Frame getFrame(int frame) {
		return this.frames[frame];
	}
	
	private void calculateVoronoiRegions(ImagePlus timedup, Objects3DPopulationSPT populationT) {
		float radMax = 1000000f; //0 for no max
		Watershed3DVoronoi watershed3DVoronoi = new Watershed3DVoronoi(ImageInt.wrap(timedup), radMax);
		ImageInt voronoiZones = watershed3DVoronoi.getVoronoiZones(false);
		populationT.setZones(voronoiZones);
	}

	private ImagePlus getTRawFrame(int t) {
		 ImagePlus timedup;
 		 timedup = ImageJStatic.getImage(directoryRaw, rawFramesFile[t].getName()).getImagePlus();
 		 return timedup;
	}
	
	@Override
	public void nextFrame() {
		currentFrame++;
	}

	public int getSize() {
		return numberOfFrames;
	}
	
	public int getFrameTime() {
		return currentFrame;
	}
	
	public TemporalPopulation3D getTemporalPopulation3D() {
		return new Object3DTracking(this.getLastFrame(), this.getFrame());
	}
	
	public ImagePlus getSegFrame() {
		return getTSegFrame(currentFrame);
	}

	public ImagePlus getTSegFrame(int t) {
		ImagePlus timedup;
		timedup = ImageJStatic.getImage(directorySeg, fileSegName+IJ.pad(t, 2)+".tif").getImagePlus();
		return timedup;
	}
	
	@Override
	public String getSegFileName() {
		return fileSegName;
	}

}
