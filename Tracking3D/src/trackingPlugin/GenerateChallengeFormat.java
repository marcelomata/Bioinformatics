package trackingPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import amal.tracking.Node;
import amal.tracking.Spot;
import amal.tracking.Tracking;
import ij.IJ;
import ij.ImagePlus;
import mcib3d.geom.Object3D;
import mcib3d.geom.ObjectCreator3D;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.image3d.ImageHandler;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingResult3DSPT;

public class GenerateChallengeFormat {
	
	private static final String FILE_NAME_RESULT_RES = "mask";

	private List<Node<Spot>> roots;
	private List<List<Node<Spot>>> nodesPerFrame;
	private int numberOfFrames;
	
	private File dirSeg;
	private File dirRes;
	
	public GenerateChallengeFormat(TrackingResult3DSPT result, File dirSeg, File dirRes, int numMaxFrames) {
		this.roots = new ArrayList<Node<Spot>>();
		this.nodesPerFrame = new ArrayList<List<Node<Spot>>>();
		this.dirSeg = dirSeg;
		this.dirRes = dirRes;
		this.numberOfFrames = result.getNumberOfFrames();
		loadRoots(result);
	}
	
	private void loadRoots(TrackingResult3DSPT result) {
		Map<Integer, List<ObjectTree3D>> objects = result.getMotionField().getFinalResultByTrack();
		Set<Integer> objectKeys = objects.keySet();
		List<Node<Spot>> listFrame;
		List<ObjectTree3D> objectsList;
		ObjectTree3D objectTree;
		Object3D obj;
		Node<Spot> node;
		Spot spot;
		int i = 0;
		
		for (Integer integer : objectKeys) {
			objectsList = objects.get(integer);
			i = 0;
			objectTree = objectsList.get(i);
			obj = objectTree.getObject();
			while(obj == null) {
				objectTree = objectsList.get(i);
				obj = objectTree.getObject();
				i++;
			}
			
			if(obj != null && objectTree.getParent() == null) {
				for (int j = nodesPerFrame.size(); j <= objectTree.getFrame(); j++) {
					nodesPerFrame.add(new ArrayList<Node<Spot>>());
				}
				listFrame = nodesPerFrame.get(objectTree.getFrame());
				
				spot = new Spot(obj, integer, objectTree.getFrame(), 0, 1);
				node = new Node<Spot>(spot);
				listFrame.add(node);
				roots.add(node);
				addChildren(objectTree, node);
			}
		}
	}
	
	private void addChildren(ObjectTree3D objectTreeParent, Node<Spot> node) {
		Spot spot;
		Object3D obj;
		Node<Spot> child;
		List<Node<Spot>> listFrame;
		List<ObjectTree3D> children = objectTreeParent.getChildren();
		for (ObjectTree3D objectTree3D : children) {
			obj = objectTree3D.getObject();
			if(obj != null) {
				for (int j = nodesPerFrame.size(); j <= objectTree3D.getFrame(); j++) {
					nodesPerFrame.add(new ArrayList<Node<Spot>>());
				}
				listFrame = nodesPerFrame.get(objectTree3D.getFrame());
				spot = new Spot(obj, objectTree3D.getId(), objectTree3D.getFrame(), 0, 1);
				child = new Node<Spot>(spot, node);
				node.addChild(child);
				addChildren(objectTree3D, child);
				listFrame.add(child);
			}
		}
	}
	
	public List<Node<Spot>> getRoots() {
		return roots;
	}
	
	public void computeColorChallenge(int start) {
        ArrayList<Node<Spot>> tmp = new ArrayList<Node<Spot>>();
        for (Node<Spot> root : roots) {
            tmp.add(root);
        }

        int c = start;

        while (!tmp.isEmpty()) {
            ArrayList<Node<Spot>> tmp2 = new ArrayList<Node<Spot>>();
            for (Node<Spot> node : tmp) {
                propagateColorToDescendant(node, c);
                c++;
                Node<Spot> desc = node.getLastDescendant();
                ArrayList<Node<Spot>> children = desc.getChildren();
                if (!children.isEmpty()) {
                    // System.out.println("child1=" + children.get(0) + " child2=" + children.get(1));
                    tmp2.add(children.get(0));
                    tmp2.add(children.get(1));
                }
            }
            tmp = tmp2;
        }
    }
	
	private void propagateColorToDescendant(Node<Spot> node, int color) {
        ArrayList<Node<Spot>> children = node.getNodesToDescendant();

        for (Node<Spot> child : children) {
            child.getData().setObjectColor(color);
        }
    }
	
	public void challengeFormat(int minLife) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(dirRes.getAbsolutePath()+"/res_track.txt");
            Log.println("Analysing splitting events...");
            ArrayList<Node<Spot>> tmp = new ArrayList<Node<Spot>>();
            for (Node<Spot> root : roots) {
                tmp.add(root);
            }
            while (!tmp.isEmpty()) {
                ArrayList<Node<Spot>> tmp2 = new ArrayList<Node<Spot>>();
                for (Node<Spot> root : tmp) {
                    Node<Spot> desc = root.getLastDescendant();
                    //pw.println("");
                    //System.out.println("root=" + root + " desc=" + desc);
                    Node<Spot> parent = root.getParent();
                    int parentlabel;
                    if (parent != null) {
                        parentlabel = root.getParent().getData().getObjectColor();
                    } else {
                        parentlabel = 0;
                    }
                    int total = desc.getData().getFrame() - root.getData().getFrame();
                    // case nb frames not reached yet 
                    if (desc.getData().getFrame() >= numberOfFrames - 1) {
                        total = minLife + 1;
                    }
                    if (total >= minLife) {
                        pw.println((root.getData().getObjectColor()) + " " + root.getData().getFrame() + " " + desc.getData().getFrame() + " " + parentlabel);
                        Log.println((root.getData().getObjectColor()) + " " + root.getData().getFrame() + " " + desc.getData().getFrame() + " " + parentlabel);
                    }
                    ArrayList<Node<Spot>> children = desc.getChildren();
                    if (!children.isEmpty()) {
                        // System.out.println("child1=" + children.get(0) + " child2=" + children.get(1));
                        tmp2.add(children.get(0));
                        tmp2.add(children.get(1));
                    }
                }
                tmp = tmp2;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Tracking.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }
	
	/**
     * Colours all the detections belonging to the same tree with the same
     * colour
     *
     * @param colorPath: the base path of the new images to be generated where
     *                   the objects are differently-coloured
     * @param pad
     * @param start      the first index to use, usually 1
     * @param minLife
     */
    public void saveColoredChallenge(int pad, int start, int minLife) {
    	Log.println("Changing colors ...");

        // Retrieve the paths to the segmented images
        // String[] paths = getSegPaths(NbPad, first);
        File fileFolder = new File(dirSeg.getAbsolutePath());
        String []frames = fileFolder.list();
        List<String> framesList = Arrays.asList(frames);
        Collections.sort(framesList);
        frames = framesList.toArray(new String[framesList.size()]);
        String fileName = frames[0];
        int i = 1;
        while(!(i >= frames.length || fileName.contains(".tif"))) {
        	fileName = frames[i];
        	i++;
        }
//        ImageHandler seg = ImageJStatic.getImageSeg(dirSeg.getAbsolutePath(), FILE_NAME_RESULT_SEG, 0, 2, 1);
        ImageHandler seg = ImageJStatic.getImageSeg(dirSeg.getAbsolutePath(), fileName, 0, 2, 1);

        // Loop through the frames in which the colour of the objects will be changed
        for (int f = 0; f < numberOfFrames; f++) {
            // The object responsible of creating the new 3D image where the cell's detections
            // will be coloured according to the colour of the first occurrence of the cell
            ObjectCreator3D creator = new ObjectCreator3D(seg.sizeX, seg.sizeY, seg.sizeZ);
//            creator.setResolution(dataset.getCalXY(), dataset.getCalZ(), "um");
            creator.setResolution(1, 1, "um");

            List<Node<Spot>> list = getNodesInFrame(f);
            double coeff = 1.2;
            for (Node<Spot> node : list) {
                //compute timelife
                int total = node.getLastDescendant().getData().getFrame() - node.getAncestor().getData().getFrame();
                if (node.getLastDescendant().getData().getFrame() <= minLife) {
                    total = minLife + 1;
                }
                if (numberOfFrames - node.getLastDescendant().getData().getFrame() <= minLife) {
                    total = minLife + 1;
                }
                Spot spot = node.getData();
                if (total >= minLife) {
                    //System.out.println("creating spot "+spot);
                    double rad = coeff * spot.getRadius();
                    creator.createEllipsoidUnit(spot.getPosX(), spot.getPosY(), spot.getPosZ(), rad, rad, rad, spot.getObjectColor(), false);
                }
            }

            // Create the new image containing the differently-coloured objects
            // Save the image under the specified folder
            IJ.saveAsTiff(new ImagePlus("Res", creator.getStack()), dirRes.getAbsolutePath() + "/" + FILE_NAME_RESULT_RES + IJ.pad(f + start, pad) + ".tif");
        }

        Log.println("End changing colors.");
    }
    
    /**
     * Colours all the detections belonging to the same tree with the same
     * colour
     *
     * @param colorPath: the base path of the new images to be generated where
     *                   the objects are differently-coloured
     */
    public void saveColored(int pad, int start) {
    	Log.println("Changing colors ...");

        // Retrieve the paths to the segmented images
        // String[] paths = getSegPaths(NbPad, first);
        File fileFolder = new File(dirSeg.getAbsolutePath());
        String []frames = fileFolder.list();
        List<String> framesList = Arrays.asList(frames);
        Collections.sort(framesList);
        frames = framesList.toArray(new String[framesList.size()]);
        int k = 0;
        String fileName = frames[k];
        while(!(k >= frames.length || frames[k].contains(".tif"))) {
        	k++;
        }

        // Loop through the frames in which the colour of the objects will be changed
        for (int i = 0; k < frames.length; k++) {
        	fileName = frames[k];
        	if(fileName.contains(".tif")) {
	            // Open the segmented image located at the specified path
	//            ImagePlus im = ImageJStatic.getImageSeg(dirSeg.getAbsolutePath(), FILE_NAME_RESULT_SEG, 0, 2, i).getImagePlus();
	            ImagePlus im = ImageJStatic.getImageSeg(dirSeg.getAbsolutePath(), fileName, 0, 2, i).getImagePlus();
	
	            // Extract the objects contained in the segmented image
	            Objects3DPopulation frame = new Objects3DPopulation(im);
	
	            // The object responsible of creating the new 3D image where the cell's detections
	            // will be coloured according to the colour of the first occurrence of the cell
	            ObjectCreator3D creator = new ObjectCreator3D(im.getImageStack());
	
	            // Extract the nodes containing the spots belonging to the current frame
	            List<Node<Spot>> nodesInFrame = getNodesInFrame(i);
	            printValuesNodes(nodesInFrame);
	            printValuesObj3D(frame.getObjectsList());
	
	            // Extract these spots' values and store them in a new list
	            ArrayList<Integer> values = new ArrayList<Integer>();
	
	            for (Node<Spot> nodesInFrame1 : nodesInFrame) {
	                values.add(nodesInFrame1.getData().value());
	            }
	
	            Log.println("Is consistent - "+checkConsistenceBetweenLists(nodesInFrame, frame.getObjectsList()));
	            
	            // Loop through the objects present in the current frame
	//            System.out.println("size values == size frame objects - "+(values.size()==frame.getNbObjects()));
	            for (int j = 0; j < frame.getNbObjects(); j++) {
	
	                // Retrieve the value of the current object
	                int value = frame.getObject(j).getValue();

	                // Look for the index of the object's value in the list containing the values of the
	                // spots belonging to the current frame
	                int index = values.indexOf(value);
	
	                // If we find in the tree the spot modelling the current object (the algorithm
	                // may delete some detections when they are likely false positives )
	                if (index != -1) {
	
	                    // Retrieve the correspondent spot
	                    Spot spot = nodesInFrame.get(index).getData();
	
	                    // Sets the object's value to the spot's color
	                    // The value of the object determines its colour
	                    if (spot.getState() != Spot.FAKE) {
	                        frame.getObject(j).setValue(spot.getObjectColor()+1);
	                        Log.println("Frame "+i+" - Indice "+index+" - Object value "+spot.value()+" - ID "+nodesInFrame.get(index).getData().getID()+
	                        			" - Object color "+spot.getObjectColor()+" - Position X "+spot.getPosX()+", Y "+spot.getPosY()+", Z "+spot.getPosX());
	                    } else {
	                        frame.getObject(j).setValue(0);
	                        Log.println("fake " + frame.getObject(j) + " in frame " + i);
	                    }
	                    // Draw the object with its new value (if it has changed) in the new image
	                    creator.drawObject(frame.getObject(j));
	                } else {
	                	Log.println("index == -1");
	                }
	
	            }
	            // Create the new image containing the differently-coloured objects
	            ImagePlus res = new ImagePlus("Res", creator.getStack());
	
	            // Save the image under the specified folder
	            IJ.saveAsTiff(res, dirRes.getAbsolutePath() + "/" + FILE_NAME_RESULT_RES + IJ.pad(i + start, pad) + ".tif");
	            Log.println("");
            	i++;
        	}
        }

        Log.println("End changing colors.");
    }
    
    private void printValuesNodes(List<Node<Spot>> nodesInFrame) {
    	for (Node<Spot> node : nodesInFrame) {
    		Log.print(node.getData().value()+" ");
		}
    	Log.println("");
	}
    
    private void printValuesObj3D(List<Object3D> obj3DList) {
    	for (Object3D obj3D : obj3DList) {
    		Log.print(obj3D.getValue()+" ");
		}
    	Log.println("");
	}

	private boolean checkConsistenceBetweenLists(List<Node<Spot>> nodesInFrame, ArrayList<Object3D> objectsList) {
    	int value;
    	for (Node<Spot> node : nodesInFrame) {
    		value = node.getData().value();
			for (Object3D object3d : objectsList) {
				if(object3d.getValue() == value) {
					return true;
				}
			}
		}
    	return false;
	}

    private List<Node<Spot>> getNodesInFrame(int frame) {
    	return nodesPerFrame.get(frame);
    }

}
