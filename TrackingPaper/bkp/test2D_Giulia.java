package amal;

import amal.tracking.Node;
import amal.tracking.Spot;
import amal.tracking.Tracking;

import java.io.File;
import java.util.ArrayList;

public class test2D_Giulia {

    public static void main(String[] args) {
        // Define dataset
        DataSet dataset = new DataSet("Test780", 780);
        String baseDir = "/home/thomasb/DATA/Tracking/Giulia/";
        String data = "780";
        String fs = File.separator;
        dataset.setDirRaw(baseDir + data + fs);
        dataset.setDirSeg(baseDir + data + fs);
        dataset.setBaseRaw("Raw/Raw-");
        dataset.setBaseSeg("Seg-1/Seg-");
        dataset.setFirstRaw(1);
        dataset.setFirstSeg(1);
        dataset.setPadRaw(3);
        dataset.setPadSeg(3);
        dataset.setCalXY(1);
        dataset.setCalZ(1);
        dataset.setCalT(1);

        int nbFrames = 780;

        // The time (in minutes) separating two consecutive frames
        double timeInterval = 1; // 1 min for 110, 10 for 384, 5 for 780

        // When two cells merge, the algorithm checks what happens in the next x frames: if they
        // are still merging together after this period of frames, the merging association is 
        // deleted because the final tree lineage should not contain merging cells. This defined
        // number of frames is called mergingLife
        int mergingLife = 10; //20

        // When a cell is not linked to any other after the algorithm executed a whole step, a
        // fake cell is created at the following time point to see if it finds a suitable link 
        // later. If so, a missed detection is corrected. However, if it does not find a cell 
        // to be linked to after a certain number of frames, the fake cells need to be deleted. 
        // The fakeLife parameter defines the number of frames where the algorithm can keep the 
        // fake cell. I also use this parameter when a cell is detected for the first time. It
        // is set to fake unless it finds a suitable link in the following frames before its age 
        // reaches fakeLife.
        int fakeLife = 15; //10

        int minLife = 10; // 20,50 do not display cells with life < minlife
        
        

        /*
         * The following performs the tracking for the objects detected in the
         * segmented images provided by the segBaseName. For example, the path 
         * to the segmented images for t = 1 is defined as segBaseName +"1.tif"
         * 
         *  A segmented images contains all the slices (for a 3D image) but only 
         *  one frame (one time point) --> it is a stack only if it is a 3D image
         */
        // The base name of the segmented images
        // The path to the segmented image is : segBaseName + i + ".tif" where i refers to the 
        // index of the considered frame
        String segBaseName = baseDir + "/Seg-1/Seg-";
        String rawBaseName = baseDir + "/Raw/Raw-";


        // To compute the motion-based search radius for every cell, the algorithm considers its
        // displacements' standard deviation on a defined number of frames in which the cell has
        // been detected. This number is bound by the spot's maximum  and minimum age to compute 
        // searchRadius1. Therefore, if the spot has just been detected, its mean displacement is
        // not considered. Likewise, there is no need to consider the displacement it performed 
        // a long time ago.
        int maxAgeRadius1 = 5;
        int minAgeRadius1 = 3;

        // To compute the local-density-based search radius for every cell, the algorithm 
        // considers the distances to its nearest neighbours in a defined number of frames in 
        // which the cell has been detected. This number is the minimum between the spot's age
        // and the following parameter: the spot's maximum age to compute searchRadius2
        int maxAgeRadius2 = 5;

        // The spot's local-density-based search radius is computed as a percentage of its
        // distance to its nearest neighbour in the frame. The percentage is fixed by 
        // searchRadius2Coef and its default value is 0.5.
        double searchRadius2Coef = 0.5;

        // For the direct daughters of a splitting cells, an alternative coefficient can be 
        // defined to compute the local-density-based search radius because the cells are too 
        // close to each other. 
        double alternativeSearchRadius2Coef = 2.75;


        // The user defines according to his dataset the weight assigned to each penalty
        double weightArea = 0;
        double weightCompactness = 0;
        double weightElongation = 0;
        double weightFlatness = 0;
        double weightSphericity = 0;
        double weightVolume = 0;
        double weightSplit = 1;
        double weightColoc = 1;

        // The spot's final search radius is bound by an upper and a lower limit depending on the
        // studied data set
        double upperLimit = 50;
        double lowerLimit = 0;

        // In the second cost matrix, the algorithm considers the cells that could not be linked
        // in the first step. The no-linking conclusino can be explained by a large distance 
        // between the cells. Therefore, the second cost matrix should consider a higher maximal 
        // distance at least for the gap closing associations. To do so, a coefficient (greater
        // than 1) is defined. The new maximal distance is the result of multiplying this 
        // coefficient to the old maximal distance. To preserve the algorithm flexibility, three
        // coefficients were defined according to the nature of the links to be made.
        double coefGapClose = 2.5;
        double coefMerge = 1.6;
        double coefSplit = 1.6;

        // To model an impossible link, a blocking value is chosen for every cost matrix. It is
        // generally set to Double.MAX_VALUE
        double blockingValue1 = Double.MAX_VALUE;
        double blockingValue2 = Double.MAX_VALUE;

        // Instantiates the class that will execute the algorithm
        Tracking algorithm = new Tracking(nbFrames, segBaseName, timeInterval,
                maxAgeRadius1, minAgeRadius1, maxAgeRadius2, searchRadius2Coef,
                alternativeSearchRadius2Coef, upperLimit, lowerLimit, coefGapClose, coefMerge,
                coefSplit, mergingLife, fakeLife, blockingValue1, blockingValue2, weightArea,
                weightCompactness, weightElongation, weightFlatness, weightSphericity,
                weightVolume,weightColoc, weightSplit);
        algorithm.dataset = dataset;

        //algorithm.setRawBaseName(rawBaseName);
        // algorithm.setCalibration(1, 1);// CALIBRATION
        algorithm.removeObjBorders = true;

        // formatting 3 numbers, starts at 1
        // algorithm.setFormat(3, 1);

        // Exectue the algorithm and retrieve the data structure containing the 
        // lineage tree: an array list of the roots of every track
        ArrayList<Node<Spot>> roots = algorithm.execute();

        algorithm.writeXML(baseDir + data + "/Seg.tif", "Raw.tif", baseDir + data, baseDir + data + "/TRACK/lineage.xml");

        /*
         * Use the forest structure to colour the cells belonging to the same tree
         * with the same colour
         * 
         */
        //algorithm.computeColorChallenge(roots, 1);
        //algorithm.challengeFormat(baseDir + data + "/TRACK/res_track.txt", minLife);
        //algorithm.analyseSplitting(baseDir + data + "/TRACK/res_split.txt", false);

        /*
         * Colours the segmented images
         * 
         * The output: a list of segmented images (a stack of slices if 3D images)
         * where the objects related to the same cell have the same gray value
         * 
         * The segmented image related to t = 1 will be stored at colorPath+"1.tif"
         */
        String colorPath = baseDir + data + "/TRACK/disk";
        //algorithm.saveColoredChallenge(colorPath, 3, 1, minLife);
        String colorPath2 = baseDir + data + "/TRACK/cell";
        //algorithm.saveColored(colorPath2, 3, 1);
        /*
         * Analyse splitting events using the forest structure 
         * 
         * The result is displayed in the text file saved at the location
         * specified by textPath 
         * 
         */
        //String textPath = baseDir + data + "TRACK/split.txt";

        //ArrayList<Node<Spot>> splittingForest = algorithm.analyseSplitting(textPath,false);


        /*
         * The next part writes the XML file 
         * 
         * The XML file is saved at the path provided by xmlName
         */
        // The path to the file where the XML will be stored
        //String xmlName = baseDir + data + "/TRACK/Tracking.xml";
        // The folder containing the original stack (needed for the XML file)
        //String stackFolder = baseDir + data + "/TRACK/";
        // The name of the original stack
        // The path to the original stack is : stackFolder + stackName
        // the original stack contains all the frames and all the slices
        //String stackName = "Original-" + nbFrames + ".tif";
        // The path to a segmented image (to retrieve the image's characteristics)
        // A segmented image is a stack containing all the slices (for 3D images) but 
        // only one frame (one time point)
        // String segPath = segBaseName + data + "/" + data + "Raw.tif";
        //algorithm.writeXML(segPath, baseDir + "01.tif", stackFolder, xmlName);

        System.out.println("Finished");
    }

}
