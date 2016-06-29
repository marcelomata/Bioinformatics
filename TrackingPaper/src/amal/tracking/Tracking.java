package amal.tracking;

import amal.DataSet;
import amal.penalties.*;
import amal.xml.TrackMate;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.Duplicator;
import mcib3d.geom.ObjectCreator3D;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.image3d.ImageHandler;
import mcib3d.utils.ArrayUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class executes the tracking algorithm.
 *
 * @author Amal Tiss
 */
public class Tracking {

    // The number of frames processed
    private final int nbFrames;

    // The base name of the segmented images
    // The path to the segmented image is : segBaseName + i + ".tif" where i refers to the 
    // index of the considered frame
    private final String segBaseName;

    // The base name of the raw images
    // The path to the raw image is : rawBaseName + i + ".tif" where i refers to the 
    // index of the considered frame
    //private String rawBaseName = null;
    // private String[] rawNames = null;
    //private double resXY = 1;
    //private double resZ = 1;
    // The time (in minutes) separating two consecutive frames
    private final double timeInterval;

    // To compute the motion-based search radius for every cell, the algorithm considers its
    // displacements' standard deviation on a defined number of frames in which the cell has
    // been detected. This number is bound by the spot's maximum  and minimum age to compute 
    // searchRadius1. Therefore, if the spot has just been detected, its mean displacement is
    // not considered. Likewise, there is no need to consider the displacement it performed 
    // a long time ago.
    private final int maxAgeRadius1;
    private final int minAgeRadius1;

    // To compute the local-density-based search radius for every cell, the algorithm 
    // considers the distances to its nearest neighbours in a defined number of frames in 
    // which the cell has been detected. This number is the minimum between the spot's age
    // and the following parameter: the spot's maximum age to compute searchRadius2
    private final int maxAgeRadius2;

    // The spot's local-density-based search radius is computed as a percentage of its
    // distance to its nearest neighbour in the frame. The percentage is fixed by 
    // searchRadius2Coef and its default value is 0.5.
    private final double searchRadius2Coef;

    // For the direct daughters of a splitting cells, an alternative coefficient can be 
    // defined to compute the local-density-based search radius because the cells are too 
    // close to each other. 
    private final double alternativeSearchRadius2Coef;

    // The spot's final search radius is bound by an upper and a lower limit depending on the
    // studied data set
    private final double upperLimit;
    private final double lowerLimit;

    // In the second cost matrix, the algorithm considers the cells that could not be linked
    // in the first step. The no-linking conclusino can be explained by a large distance 
    // between the cells. Therefore, the second cost matrix should consider a higher maximal 
    // distance at least for the gap closing associations. To do so, a coefficient (greater
    // than 1) is defined. The new maximal distance is the result of multiplying this 
    // coefficient to the old maximal distance. To preserve the algorithm flexibility, three
    // coefficients were defined according to the nature of the links to be made.
    private final double coefGapClose;
    private final double coefMerge;
    private final double coefSplit;

    // When two cells merge, the algorithm checks what happens in the next x frames: if they
    // are still merging together after this period of frames, the merging association is 
    // deleted because the final tree lineage should not contain merging cells. This defined
    // number of frames is called mergingLife
    private final int mergingLife;

    // When a cell is not linked to any other after the algorithm executed a whole step, a 
    // fake cell is created at the following time point to see if it finds a suitable link 
    // later. If so, a missed detection is corrected. However, if it does not find a cell 
    // to be linked to after a certain number of frames, the fake cells need to be deleted. 
    // The fakeLife parameter defines the number of frames where the algorithm can keep the 
    // fake cell. I also use this parameter when a cell is detected for the first time. It
    // is set to fake unless it finds a suitable link in the following frames before its age 
    // reaches fakeLife.
    private final int fakeLife;

    // To model an impossible link, a blocking value is chosen for every cost matrix. It is
    // generally set to Double.MAX_VALUE
    private final double blockingValue1;
    private final double blockingValue2;

    // The user defines according to its dataset the weight assigned to each penalty
    private final double weightArea;
    private final double weightCompactness;
    private final double weightElongation;
    private final double weightFlatness;
    private final double weightSphericity;
    private final double weightVolume;
    private final double weightColoc;
    private final double weightSplit;

    // The graph containing the lineage tree
    private final LineageTree graph;

    // The forest containing the lineage tree
    private final ArrayList<Node<Spot>> roots;

    // formatting names
    //int NbPad = 2;
    //int first = 0;

    // borders
    public boolean removeObjBorders = false;
    // dataset
    public DataSet dataset;
    // analyse diff beetween split
    ArrayList<Double> diffs;

    /**
     * Constructor
     *
     * @param nbFrames:                     the number of images to be processed
     * @param segBaseName:                  the base name of the segmented images
     * @param timeInterval:                 the time separating two consecutive frames
     * @param maxAgeRadius1:                the maximum number of frames considered to compute
     *                                      the cell's displacements' standard deviation
     * @param minAgeRadius1:                the minimum number of frames where the cell must be
     *                                      detected to compute its displacements' standard deviation
     * @param maxAgeRadius2:                the maximum number of frames considered to compute
     *                                      the mean value of the cell's distance to its nearest neighbour in the
     *                                      frame
     * @param searchRadius2Coef:            the percentage of the distance to the nearest
     *                                      neighbour considered to compute the local-density-based search radius
     * @param alternativeSearchRadius2Coef: the alternative percentage to
     *                                      compute the local- density-based search radius for the direct daughters
     *                                      of a splitting cell
     * @param upperLimit:                   the maximum value of every spot's search radius
     * @param lowerLimit:                   the minimum value of every spot's search radius
     * @param coefGapClose:                 the coefficient considered to compute a higher
     *                                      maximal distance in the second cost matrix for gap closing associations
     * @param coefMerge:                    the coefficient considered to compute a higher maximal
     *                                      distance in the second cost matrix for merging associations
     * @param coefSplit:                    the coefficient considered to compute a higher maximal
     *                                      distance in the second cost matrix for splitting associations
     * @param mergingLife:                  the maximal number of frames where two merging cells
     *                                      will be kept by the algorithm
     * @param fakeLife:                     the maximal number of frames where a fake cell is spread
     * @param blockingValue1:               the cost of an impossible link in the first cost
     *                                      matrix
     * @param blockingValue2:               the cost of an impossible link in the second cost
     *                                      matrix
     * @param weightArea:                   the weight assigned to the area penalty
     * @param weightCompactness:            the weight assigned to the compactness penalty
     * @param weightElongation:             the weight assigned to the elongation penalty
     * @param weightFlatness:               the weight assigned to the flatness penalty
     * @param weightSphericity:the          weight assigned to the sphericity penalty
     * @param weightVolume:                 the weight assigned to the volume penalty
     * @param weightSplit:                  the weight assigned to the penalty concerning
     *                                      splitting events
     */
    public Tracking(int nbFrames, String segBaseName,
                    double timeInterval, int maxAgeRadius1, int minAgeRadius1,
                    int maxAgeRadius2, double searchRadius2Coef, double alternativeSearchRadius2Coef,
                    double upperLimit, double lowerLimit, double coefGapClose, double coefMerge,
                    double coefSplit, int mergingLife, int fakeLife, double blockingValue1,
                    double blockingValue2, double weightArea, double weightCompactness, double weightElongation, double weightFlatness, double weightSphericity, double weightVolume, double wc, double weightSplit) {

        this.nbFrames = nbFrames;

        this.segBaseName = segBaseName;

        this.timeInterval = timeInterval;

        this.maxAgeRadius1 = maxAgeRadius1;
        this.minAgeRadius1 = minAgeRadius1;

        this.maxAgeRadius2 = maxAgeRadius2;

        this.searchRadius2Coef = searchRadius2Coef;

        this.alternativeSearchRadius2Coef = alternativeSearchRadius2Coef;

        this.upperLimit = upperLimit;
        this.lowerLimit = lowerLimit;

        this.coefGapClose = coefGapClose;
        this.coefMerge = coefMerge;
        this.coefSplit = coefSplit;

        this.mergingLife = mergingLife;
        this.fakeLife = fakeLife;

        this.blockingValue1 = blockingValue1;
        this.blockingValue2 = blockingValue2;

        this.weightArea = weightArea;
        this.weightCompactness = weightCompactness;
        this.weightElongation = weightElongation;
        this.weightFlatness = weightFlatness;
        this.weightSphericity = weightSphericity;
        this.weightVolume = weightVolume;
        this.weightColoc = wc;
        this.weightSplit = weightSplit;

        this.graph = new LineageTree();

        this.roots = new ArrayList<Node<Spot>>();
    }

    /**
     * Generates the paths to the segmented images from the base name and the
     * number of frames
     *
     * @return an array containing the paths to the considered segmented images
     */
    private String[] getSegPaths(int nbPad, int first) {

        // Instantiate the paths array
        String[] segPaths = new String[nbFrames];

        // Loop through the segmented images
        for (int i = 0; i < nbFrames; i++) {

            // Store the path to the segmented image at the suitable position in the array 
            segPaths[i] = segBaseName + IJ.pad(i + first, nbPad) + ".tif";

        }

        return segPaths;
    }

//    private String[] getRawPaths(int nbPad, int first) {
//
//        // Instantiate the paths array
//        String[] rawPaths = new String[nbFrames];
//
//        // Loop through the segmented images
//        for (int i = 0; i < nbFrames; i++) {
//
//            // Store the path to the segmented image at the suitable position in the array 
//            rawPaths[i] = rawBaseName + IJ.pad(i + first, nbPad) + ".tif";
//
//        }
//
//        return rawPaths;
//    }

    /**
     * Defines the penalties used in the first cost matrix
     *
     * @return the penalties used in the first cost matrix
     */
    private Penalties getPenalties1() {
        Penalty compactness = new PenaltyCompactness(weightCompactness);
        Penalty flatness = new PenaltyFlatness(weightFlatness);
        Penalty sphericity = new PenaltySphericity(weightSphericity);
        Penalty volume = new PenaltyVolume(weightVolume);
        Penalty coloc = new PenaltyColoc(weightColoc);

        Penalties penalties1 = new Penalties();

        //penalties1.add(compactness);
        //penalties1.add(sphericity);
        //penalties1.add(flatness);
        penalties1.add(coloc);

        return penalties1;
    }

    /**
     * Defines the penalties used in the second cost matrix
     *
     * @return the penalties used in the second cost matrix
     */
    private Penalties getPenalties2() {
        // For the second cost matrix, it is possible to consider volume and area penalties
        // because the nature of association to be made is known allowing the algorithm to 
        // chose the right formula for computing the penalties. For example, the volume 
        // penalty is computed using the volume of both the daughters cells for a possible
        // splitting link.
        Penalty area = new PenaltyArea(weightArea);
        Penalty compactness = new PenaltyCompactness(weightCompactness);
        Penalty elongation = new PenaltyElongation(weightElongation);
        Penalty flatness = new PenaltyFlatness(weightFlatness);
        Penalty sphericity = new PenaltySphericity(weightSphericity);
        Penalty volume = new PenaltyVolume(weightVolume);
        Penalty coloc = new PenaltyColoc(weightColoc);

        // Of course, for the second cost matrix, we should use the splitting penalty that 
        // incorporates our knowledge about the cells' behaviour while splitting.
        Penalty split = new PenaltySplit(weightSplit);

        Penalties penalties2 = new Penalties();

        // penalties2.add(area);
        // penalties2.add(compactness);
        // penalties2.add(elongation);
        // penalties2.add(flatness);
        // penalties2.add(sphericity);
        // penalties2.add(volume);
        penalties2.add(coloc);
        penalties2.add(split);

        return penalties2;
    }

//    public void setFormat(int NbPad0, int first0) {
//        NbPad = NbPad0;
//        first = first0;
//    }

    public ArrayList<Node<Spot>> execute() {
        // test
        int frame = 0;

        init();

        ImagePlus tmpPlus = dataset.getImageSegPlus(frame);
        Objects3DPopulation frame0 = new Objects3DPopulation(tmpPlus);

        graph.init(frame0);


        graph.updateSpots();

        tmpPlus = dataset.getImageSegPlus(frame + 1);
        Objects3DPopulation frame1 = new Objects3DPopulation(tmpPlus);

        Distance distance = new Distance(Double.MAX_VALUE, frame0, frame1);
        Penalties penalties1 = getPenalties1();

        firstStep(frame1, 1, distance, penalties1);


        for (Spot spot : graph.getLinked1())
            IJ.log("L1 ID" + spot.getID() + "-" + spot.value() + " " + graph.getChildren(spot));
        IJ.log("");
        for (Spot spot : graph.getUnlinked1()) {
            IJ.log("U1 ID" + spot.getID() + "-" + spot.value() + " " + spot);
            ArrayUtil arrayUtil = spot.getObject3D().listValues(dataset.getImageSeg(frame + 1)).distinctValues();
            if (arrayUtil.getSize() > 1) IJ.log("U1 " + arrayUtil);
            else
                IJ.log("U1 " + frame1.kClosestBorder(spot.getObject3D(), 1));
        }
        IJ.log("");
        //for (Spot spot : graph.getLinked2())
        //    IJ.log("L2 ID" + spot.getID() + "-" + spot.value() + " " + graph.getParent(spot));
        for (Spot spot : graph.getUnlinked2()) {
            IJ.log("U2 ID" + spot.getID() + "-" + spot.value() + " " + spot);
            ArrayUtil arrayUtil = spot.getObject3D().listValues(dataset.getImageSeg(frame)).distinctValues();
            if (arrayUtil.getSize() > 1) IJ.log("U2 " + arrayUtil);
            else
                IJ.log("U2 " + frame0.kClosestBorder(spot.getObject3D(), 1));
        }

        /////////////////// STEP 2
        //Penalties penalties2 = getPenalties2();
        // secondStep(distance, penalties2);

        return roots;
    }



    /**
     * This method executes the tracking algorithm.
     *
     * @return the Track Mate model used for the XML file
     */
    public ArrayList<Node<Spot>> executeOK() {

        // Initialises the tracking algorithm : defines the lineage tree parameters based on 
        // the values entered by the user.
        init();

        // Defines the penalties considered for the first cost matrix
        Penalties penalties1 = getPenalties1();

        // Defines the penalties considered for the second cost matrix
        Penalties penalties2 = getPenalties2();

//        if (rawBaseName != null) {
//            rawNames = getRawPaths(NbPad, first);
//        }
        
        
        Objects3DPopulation frame;
        Objects3DPopulation lastFrame = null;
        // Loop through the number of frames considered
        for (int i = 0; i < nbFrames; i++) {
            // Retrieves the objects contained in every segmented image
            //IJ.log("opening seg" + i);
            //ImagePlus tmpPlus = new ImagePlus(segPaths[i]);
            ImagePlus tmpPlus = dataset.getImageSegPlus(i);
        	
            
            frame = new Objects3DPopulation(tmpPlus);
            // THOMAS remove objects touching borders
            if (removeObjBorders) {
                frame.removeObjectsTouchingBorders(tmpPlus, false);
            }

            // If it is the first frame (the graph does not contain any spots)
            if (graph.isEmpty()) {
                // Initialises the graph with the spots contained in the first frame
                //IJ.log("opening raw " + i);
                ImageHandler img = dataset.getImageRaw(i);
                if (img != null) {
                    graph.init(frame, img);
                } else {
                    graph.init(frame);
                }
                // Update the spots created (computes their search radii)
                graph.updateSpots();
            } // If the graph already contains spots
            else {
            	// Gets the paths of the segmented images
                //String[] segPaths = getSegPaths(NbPad, first);
                // Stores the distances between every pair of cells considered. 
                // Defines the maximal distance allowed for a link to be considered and the blocking 
                // value beyond which a link is too costly to be selected.
                Distance distance = new Distance(Double.MAX_VALUE, frame, lastFrame);
            	
                // Executes the first step: consider the easy links between the spots
                // recently-added to the graph and those contained in the current frame
                firstStep(frame, i, distance, penalties1);


                // Executes the second step: consider the complicated links (splitting and 
                // merging associations) between the spots recently-added to the graph and 
                // those contained in the current frame
                secondStep(distance, penalties2);

                // Checks the associations made and modifies them if necessary to correct 
                // erroneous detections
                thirdStep();
            }
            lastFrame = frame;
        }

        // Terminates the pending associations (merging and fake spots aged less than 
        // mergingLife and fakeLife)
        end();

        setRoots();

        return roots;

    }

//    public void setRawBaseName(String rawBaseName) {
//        this.rawBaseName = rawBaseName;
//    }
//    public void setCalibration(double xy, double z) {
//        resXY = xy;
//        resZ = z;
//    }

    /**
     * Initialises the tracking algorithm by setting the Lineage Tree parameters
     * to those defined by the user
     */
    private void init() {

        graph.setTimeInterval(timeInterval);

        graph.setMaxAgeRadius1(maxAgeRadius1);
        graph.setMinAgeRadius1(minAgeRadius1);

        graph.setMaxAgeRadius2(maxAgeRadius2);

        graph.setSearchRadius2Coef(searchRadius2Coef);
        graph.setAlternativeCoef(alternativeSearchRadius2Coef);

        graph.setMergingLife(mergingLife);
        graph.setFakeLife(fakeLife);

    }

    /**
     * Performs the first step of the algorithm for every frame considered.
     *
     * @param frame      : the spots in the frame (t+1) to be added to the graph
     * @param distance   : defines the blocking value and the maximal distance
     *                   allowed for a link to be made
     * @param penalties1 : the penalties considered to compute the linking cost
     */
    private int[] firstStep(Objects3DPopulation frame, int nbframe, Distance distance, Penalties penalties1) {

        // The spot in the frame (t)
        ArrayList<Spot> spots1 = graph.leafsInLastFrame();

        // Add the spots in the frame(t+1) to the graph
        ArrayList<Spot> spots2;
        ImageHandler img = dataset.getImageRaw(0);
        if (img != null) {
            //IJ.log("opening raw 0");
            spots2 = graph.addFrame(frame, img);
        } else {
            spots2 = graph.addFrame(frame);
        }

        // Compute the linking costs between spots1 and spots2 using Cost Matrix 1
        CostMatrix1 costMatrix1 = new CostMatrix1(spots1, spots2, penalties1.get(), distance, upperLimit, lowerLimit, blockingValue1);
        double[][] costs1 = costMatrix1.getCost();

        // Solve the Linear Assignment Problem defined by costs1 
        int[] assignmentVector = new HungarianAlgorithm(costs1).execute();

        // LOG THE ASSIGNMENT

        //for (int i : assignmentVector) System.out.print((i) + "-" + (assignmentVector[i]) + " * ");

        // Make the associations according to the solution provided by the Hungarian 
        // Algorithm
        graph.addEdgesStep1(spots1, spots2, assignmentVector, costs1);

        return assignmentVector;

    }

    /**
     * Performs the second step of the algorithm for every frame considered.
     *
     * @param distance   : defines the blocking value and the maximal distance
     *                   allowed for a link to be made
     * @param penalties2 : the penalties considered to compute the linking cost
     */
    private void secondStep(Distance distance, Penalties penalties2) {

        // The spots in the frame (t) that were already linked in the first step
        ArrayList<Spot> linkedT0 = graph.getLinked1();

        // The spots in the frame (t+1) that were already linked in the first step
        ArrayList<Spot> linkedT1 = graph.getLinked2();

        // The spots in the frame (t) that were not linked in the first step
        ArrayList<Spot> unlinkedT0 = graph.getUnlinked1();

        // The spots in the frame (t+1) that were not linked in the first step
        ArrayList<Spot> unlinkedT1 = graph.getUnlinked2();

        // The spots in the frame (t+1) to which the linked spots in the frame (t) were 
        // linked in the first step
        ArrayList<Spot> linkedT0Children = graph.getLinked1Children();

        // The spots in the frame (t) to which the linked spots in the frame (t+1) were 
        // linked in the first step
        ArrayList<Spot> linkedT1Parents = graph.getLinked2Parents();

        // Compute the linking costs between the spots in the frame (t) and those in the 
        // frame (t+1) using Cost Matrix 2
        double[][] costs2 = new CostMatrix2(linkedT0, linkedT1, unlinkedT0, unlinkedT1,
                linkedT1Parents, linkedT0Children, penalties2.get(), distance, coefGapClose,
                coefMerge, coefSplit, upperLimit, lowerLimit, blockingValue2).getCost();

        // Solve the Linear Assignment Problem defined by costs2
        int[] assignmentVector = new HungarianAlgorithm(costs2).execute();

        // Make the associations according to the solution provided by the Hungarian 
        // Algorithm		
        graph.addEdgesStep2(assignmentVector, costs2);
    }

    /**
     * Performs the third step of the algorithm : analyses the associations and
     * corrects them if necessary
     */
    private void thirdStep() {
        // update the spots created or modified by the algorithm's first and second steps
        graph.updateSpots();
    }

    /**
     * Terminates the pending associations (merging and fake spots aged less
     * than mergingLife and fakeLife)
     */
    private void end() {
        graph.end();
    }

    public ArrayList<Node<Spot>> getRoots() {
        return roots;
    }

    private void addChild(Node<Spot> root) {

        Spot data = root.getData();

        ArrayList<Spot> children = graph.getChildren(data);

        for (Spot children1 : children) {
            Node<Spot> child = new Node<Spot>(children1, root);
            root.addChild(child);
            addChild(child);
        }
    }

    private void setRoots() {

        ArrayList<Spot> firstSpots = graph.getRoots();

        for (Spot firstSpot : firstSpots) {
            Node<Spot> root = new Node<Spot>(firstSpot);
            addChild(root);
            roots.add(root);
        }
    }

    /**
     * Writes the XML file to be opened with TrackMate
     *
     * @param segPath:     the path to a segmented image
     * @param stackName:   the name of the original stack
     * @param stackFolder: the folder containing the original stack
     * @param xmlName:     the name of the XML file to be written
     * @return the Track Mate model used for the XML file
     */
    public void writeXML(String segPath, String stackName, String stackFolder, String xmlName) {

        // Opens the image located at segPath 
        ImagePlus im = new ImagePlus(segPath);

        // Retrieves the number of slices in the stack
        int nbSlices = im.getNSlices();

        // Retrieves the width of the image
        int width = im.getWidth();

        // Retrieves the height of the image
        int height = im.getHeight();

        // Retrieves the pixel width
        double pixelWidth = im.getCalibration().pixelWidth;

        // Retrieves the pixel height
        double pixelHeight = im.getCalibration().pixelHeight;

        // Retrieves the voxel depth
        double voxelDepth = im.getCalibration().pixelDepth;

        System.out.println("Writing XML ... ");

        // Creates the class necessary to generate the XML file
        TrackMate trackMate = new TrackMate();

        // Sets the attribute of trackMate
        trackMate.setGuiState();
        trackMate.setVersion("2.7.3");
        trackMate.setModel(graph);
        trackMate.setSettings(stackName, stackFolder, width, height, nbSlices, nbFrames,
                pixelWidth, pixelHeight, voxelDepth, timeInterval);

        try {

            // Create the XML file under the name entered by the user
            File file = new File(xmlName);

            JAXBContext jaxbContext = JAXBContext.newInstance(TrackMate.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(trackMate, file);

        } catch (JAXBException e) {
            e.printStackTrace();
        }

        System.out.println("End XML.");

    }

    private ArrayList<Node<Spot>> getNodesInFrame(Node<Spot> root, int frame) {
        ArrayList<Node<Spot>> nodesInFrame = new ArrayList<Node<Spot>>();

        if (root.getData().getFrame() < frame) {
            ArrayList<Node<Spot>> children = root.getChildren();
            for (Node<Spot> child : children) {
                nodesInFrame.addAll(getNodesInFrame(child, frame));
            }
        } else if (root.getData().getFrame() == frame) {
            nodesInFrame.add(root);
        }

        return nodesInFrame;
    }

    private ArrayList<Node<Spot>> getNodesInFrame(int frame) {

        ArrayList<Node<Spot>> nodesInFrame = new ArrayList<Node<Spot>>();

        for (Node<Spot> root : roots) {
            nodesInFrame.addAll(getNodesInFrame(root, frame));
        }

        return nodesInFrame;

    }

    private void propagateColorToLeaf(Node<Spot> node, int color) {
        node.getData().setObjectColor(color);

        ArrayList<Node<Spot>> children = node.getChildren();

        for (Node<Spot> children1 : children) {
            propagateColorToLeaf(children1, color);
        }
    }

    private void propagateColorToDescendant(Node<Spot> node, int color) {
        ArrayList<Node<Spot>> children = node.getNodesToDescendant();

        for (Node<Spot> child : children) {
            child.getData().setObjectColor(color);
        }
    }

    public void computeColorLineage(ArrayList<Node<Spot>> roots) {
        for (int i = 0; i < roots.size(); i++) {
            propagateColorToLeaf(roots.get(i), i + 1);
        }
    }

    public void computeColorChallenge(ArrayList<Node<Spot>> roots, int start) {
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
    public void saveColoredChallenge(String colorPath, int pad, int start, int minLife) {
        System.out.println("Changing colors ...");

        // Retrieve the paths to the segmented images
        // String[] paths = getSegPaths(NbPad, first);
        ImageHandler seg = dataset.getImageSeg(0);

        // Loop through the frames in which the colour of the objects will be changed
        for (int f = 0; f < nbFrames; f++) {
            // The object responsible of creating the new 3D image where the cell's detections
            // will be coloured according to the colour of the first occurrence of the cell
            ObjectCreator3D creator = new ObjectCreator3D(seg.sizeX, seg.sizeY, seg.sizeZ);
            creator.setResolution(dataset.getCalXY(), dataset.getCalZ(), "um");

            ArrayList<Node<Spot>> list = getNodesInFrame(f);
            double coeff = 1.2;
            for (Node<Spot> node : list) {
                //compute timelife
                int total = node.getLastDescendant().getData().getFrame() - node.getAncestor().getData().getFrame();
                if (node.getLastDescendant().getData().getFrame() <= minLife) {
                    total = minLife + 1;
                }
                if (nbFrames - node.getLastDescendant().getData().getFrame() <= minLife) {
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
            IJ.saveAsTiff(new ImagePlus("Res", creator.getStack()), colorPath + IJ.pad(f + start, pad) + ".tif");
        }

        System.out.println("End changing colors.");
    }

    /**
     * Colours all the detections belonging to the same tree with the same
     * colour
     *
     * @param colorPath: the base path of the new images to be generated where
     *                   the objects are differently-coloured
     */
    public void saveColored(String colorPath, int pad, int start) {
        System.out.println("Changing colors ...");

        // Retrieve the paths to the segmented images
        // String[] paths = getSegPaths(NbPad, first);

        // Loop through the frames in which the colour of the objects will be changed
        for (int i = 0; i < nbFrames; i++) {
            // Open the segmented image located at the specified path
            ImagePlus im = dataset.getImageSegPlus(i);

            // Extract the objects contained in the segmented image
            Objects3DPopulation frame = new Objects3DPopulation(im);

            // The object responsible of creating the new 3D image where the cell's detections
            // will be coloured according to the colour of the first occurrence of the cell
            ObjectCreator3D creator = new ObjectCreator3D(im.getImageStack());

            // Extract the nodes containing the spots belonging to the current frame
            ArrayList<Node<Spot>> nodesInFrame = getNodesInFrame(i);

            // Extract these spots' values and store them in a new list
            ArrayList<Integer> values = new ArrayList<Integer>();

            for (Node<Spot> nodesInFrame1 : nodesInFrame) {
                values.add(nodesInFrame1.getData().value());
            }

            // Loop through the objects present in the current frame
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
                        frame.getObject(j).setValue(spot.getObjectColor());
                    } else {
                        frame.getObject(j).setValue(0);
                        System.out.println("fake " + frame.getObject(j) + " in frame " + i);
                    }
                    // Draw the object with its new value (if it has changed) in the new image
                    creator.drawObject(frame.getObject(j));
                }

            }
            // Create the new image containing the differently-coloured objects
            ImagePlus res = new ImagePlus("Res", creator.getStack());

            // Save the image under the specified folder
            IJ.saveAsTiff(res, colorPath + IJ.pad(i + start, pad) + ".tif");
        }

        System.out.println("End changing colors.");
    }

    private Node<Spot> getSplittingTree(Node<Spot> node, boolean isResultOfSplitting) {
        if (node == null) {
            return null;
        } else {
            ArrayList<Node<Spot>> children = node.getChildren();
            boolean division = children.size() == 2;
            if (isResultOfSplitting || division) {
                Node<Spot> newNode = new Node<Spot>(node.getData());
                for (Node<Spot> children1 : children) {
                    Node<Spot> child = getSplittingTree(children1, division);
                    if (child != null) {
                        newNode.addChild(child);
                    }
                }
                return newNode;
            } else {
                if (children.size() == 1) {
                    return getSplittingTree(children.get(0), false);
                } else {
                    return null;
                }
            }
        }
    }

    public ArrayList<Node<Spot>> analyseSplitting(String textPath, boolean useCalib) {
        diffs = new ArrayList<Double>();
        System.out.println("Analysing splitting events...");
        ArrayList<Node<Spot>> splitting = new ArrayList<Node<Spot>>();
        for (Node<Spot> root : roots) {
            Node<Spot> newNode = getSplittingTree(root, false);
            if (newNode != null) {
                splitting.add(newNode);
            } // no splitting
            else {
                // IJ.log("object " + root.getData().objectColor() + " -- " + root.getLastDescendant().getData().objectColor());
                // IJ.log(root.getData().getFrame() + " -- " + root.getLastDescendant().getData().getFrame());
            }
        }

        try {
            PrintWriter text = new PrintWriter(textPath, "UTF-8");
            for (Node<Spot> splitting1 : splitting) {
                text.println("Processing the cell with the gray value " + String.format("%3d", splitting1.getData().getObjectColor()) + " : ");
                text.println();
                display(splitting1, text, useCalib);
                text.println();
                text.println("----------------------------------------"
                        + "----------------------------------------"
                        + "----------------------------------------");
                text.println();
            }
            text.close();
        } catch (FileNotFoundException e) {
        } catch (UnsupportedEncodingException e) {
        }
        System.out.println("End.");

        String diffPath = textPath.replaceFirst("split", "diff");

        try {
            PrintWriter text = new PrintWriter(diffPath, "UTF-8");
            text.printf("diff\n");
            for (Double i : diffs) {
                text.println("" + i);
            }
            text.close();
        } catch (FileNotFoundException e) {
        } catch (UnsupportedEncodingException e) {
        }

        return splitting;
    }

    public void challengeFormat(String filename, int minLife) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(filename);
            System.out.println("Analysing splitting events...");
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
                    if (desc.getData().getFrame() >= nbFrames - 1) {
                        total = minLife + 1;
                    }
                    if (total >= minLife) {
                        pw.println((root.getData().getObjectColor()) + " " + root.getData().getFrame() + " " + desc.getData().getFrame() + " " + parentlabel);
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

    private void print(String prefix, Node<Spot> node, boolean isTail, PrintWriter text, boolean useCalib) {
        // position in unit by default
        double px = node.getData().getPosX();
        double py = node.getData().getPosY();
        double pz = node.getData().getPosZ();
        if (!useCalib) {
            px /= dataset.getCalXY();
            py /= dataset.getCalXY();
            pz /= dataset.getCalZ();
        }

        Node<Spot> par = node.getParent();
        double T0 = 0;
        if (par != null) {
            T0 = par.getData().getFrame();
        }
        double T1 = node.getData().getFrame();
        double diff;
        String dis = "";
        if ((par == null) || (T0 == 0)) {
            diff = -1;
            dis = "";
        } else {
            T0 = par.getData().getFrame();
            diff = T1 - T0;
        }
        if (diff > 1) {
            diff = node.getData().getPosT() - par.getData().getPosT();
            diffs.add(diff);
            dis = ", diff=" + String.format("%5.1f", diff);
        }

        text.println(prefix + (isTail ? "└── " : "├── ")
                + "(X=" + String.format("%5.1f", px)
                + ", Y=" + String.format("%5.1f", py)
                + ", Z=" + String.format("%3.1f", pz)
                + ", t=" + String.format("%3.1f", node.getData().getPosT())
                + " min, i=" + String.format("%3d", node.getData().getFrame())
                + dis
                + ")");
//        if (graph.getParent(node.getData()) != null) {
//            IJ.log("parent=" + graph.getParent(node.getData()));
//            IJ.log("child=" + node.getData());
//        }

        ArrayList<Node<Spot>> children = node.getChildren();
        for (int i = 0; i < children.size() - 1; i++) {
            print(prefix + (isTail ? "    " : "│   "), children.get(i), false, text, useCalib);
        }

        if (children.size() > 0) {
            print(prefix + (isTail ? "    " : "│   "), children.get(children.size() - 1), true, text, useCalib);
        }
    }

    private void display(Node<Spot> root, PrintWriter text, boolean useCalib) {
        print("", root, true, text, useCalib);
    }
}
