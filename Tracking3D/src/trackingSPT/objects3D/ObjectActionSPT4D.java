package trackingSPT.objects3D;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.Duplicator;
import mcib3d.geom.Objects3DPopulation;
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
	private String fileRawName;
	private ImagePlus file;
	private int numberOfFrames; 
	private int currentFrame;
	private File []framesFile;
	private File []rawFramesFile;
	
//	/**
//	 * 
//	 * @param imp It has to be a 4D image (3D+t)
//	 */
//	public ObjectActionSPT4D(ImagePlus imp) {
//		init();
//		this.file = imp;
//		loadFrames3D(imp);
//	}
	
	/**
	 * 
	 * @param imp It has to be a 4D image (3D+t)
	 */
	public ObjectActionSPT4D(String folderSeg, String fileSegName, String folderRaw, String fileRawName) {
		init();
		this.fileSegName = fileSegName;
		this.fileRawName = fileRawName;
		this.directorySeg = new File(folderSeg);
		this.directoryRaw = new File(folderRaw);
		loadRawFrames3D(folderRaw);
	}
	
	private void init() {
		this.population3DPlusT = new ArrayList<Objects3DPopulationSPT>();
		this.currentFrame = 0;
		this.frameByFrame = false;
		this.readFromDirectory = false;
	}

//	private void loadFrames3D(ImagePlus imp) {
//		// extract each time 
//		this.readFromDirectory = false;
//        Duplicator dup = new Duplicator();
//        int[] dim = imp.getDimensions();
//        ImagePlus timedup;
//        Objects3DPopulationSPT populationT;
//        numberOfSlices = imp.getNFrames();
//        try {
//	        for (int t = 0; t < imp.getNFrames(); t++) {
//	        	timedup = dup.run(imp, 1, 1, 1, dim[3], t, t);
//	//        	FileSaver fileSave = new FileSaver(imp);
//	//        	fileSave.saveAsTiff("/home/marcelodmo/Documents/data/simulated_15f/simulated_15f-"+(t+1)+".tif");
//	        	populationT = new Objects3DPopulationSPT(new Objects3DPopulation(timedup));
//	        	population3DPlusT.add(populationT);
//			}
//        } catch(OutOfMemoryError e) {
//        	frameByFrame = true;
//        }
//	}
	
	private void loadRawFrames3D(String folder) {
		this.readFromDirectory = true;
        File fileFolder = new File(folder);
        rawFramesFile = fileFolder.listFiles();
        List<File> framesList = Arrays.asList(rawFramesFile);
        Collections.sort(framesList);
        rawFramesFile = framesList.toArray(new File[framesList.size()]);
        countFrames();
        frameByFrame = true;
//        ImagePlus timedup;
//        Objects3DPopulationSPT populationT;
//        try {
//	        for (int t = 0, i = 0; i < frames.length; i++) {
//	        	if(frames[i].getAbsolutePath().contains(".tif")) {
//	        		fileName = frames[i].getName();
//	        		timedup =  ImageJStatic.getImageSeg(directory, fileName, t).getImagePlus();
//		            populationT = new Objects3DPopulationSPT(new Objects3DPopulation(timedup));
//		        	population3DPlusT.add(populationT);
//		        	System.out.println("Number objects frame "+t+" -> "+populationT.getObject3D().getNbObjects());
//		        	t++;
//		        	numberOfFrames++;
//	        	}
//			}
//        } catch(OutOfMemoryError e) {
//        	frameByFrame = true;
//        	population3DPlusT.clear();
//        }
	}

	private void countFrames() {
		for (int i = 0; i < rawFramesFile.length; i++) {
			if(rawFramesFile[i].getName().contains(".tif")) {
				numberOfFrames++;
			}
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
		return populationT;
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
