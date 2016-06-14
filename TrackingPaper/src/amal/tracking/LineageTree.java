package amal.tracking;

import amal.penalties.Penalty;
import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.image3d.ImageHandler;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * TODO write a general presentation of this class
 *
 * @author Amal Tiss
 */
public class LineageTree {

    // The number of created spots (it is also used as an ID for the spots)
    private static int nbCreatedSpots = 0;
    // The number of real edges in the graph
    public int nbRealEdges;
    // The current displacement second moment
    // I call it second moment but in reality, I consider the sum of squares of differences
    // between the all the cells' displacements and the current mean displacement
    // https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Online_algorithm
    public double currentMoment2Displacement = 0;
    // The structure modelling the lineage tree: a simple weighted graph where the vertices
    // are the cells and the edges the links between the cells.
    private SimpleWeightedGraph<Spot, Edge> graph;
    // The spots without children: end of tracks.
    private ArrayList<Spot> leafs;
    // The spots without parents: start of tracks
    private ArrayList<Spot> roots;
    // The spots added instead of missed detections
    private ArrayList<Spot> addedSpots;
    // TODO find a better way to map the merging and the cloned spots. My solution seems
    // redundant. 
    // The spots deleted since they are considered as false positive
    private ArrayList<Spot> deletedSpots;
    // How to find the cloned spot assigned to a merging spot
    private HashMap<Spot, Spot> mergingToCloned;
    // How to find the merging spot assigned to a cloned spot
    private HashMap<Spot, Spot> clonedToMerging;
    // The spots in the frame (t) that were already linked in the first step
    private ArrayList<Spot> linked1;
    // The spots in the frame (t+1) that were already linked in the first step
    private ArrayList<Spot> linked2;
    // The spots in the frame (t) that were not linked in the first step
    private ArrayList<Spot> unlinked1;
    // The spots in the frame (t+1) that were not linked in the first step
    private ArrayList<Spot> unlinked2;
    // The spots in the frame (t+1) to which the linked spots in the frame (t) were
    // linked in the first step
    private ArrayList<Spot> linked1Children;
    // The spots in the frame (t) to which the linked spots in the frame (t+1) were
    // linked in the first step
    private ArrayList<Spot> linked2Parents;
    // the level of the graph (the index of the last frame processed)
    private int level;
    // The time (in minutes) separating two consecutive frames
    private double timeInterval;
    // The current mean displacement of all the cells in the graph
    private double currentMeanDisplacement = 0;
    // The upper limit for every spot's search radius
    private double searchRadiusUpperLimit = 0;
    // The coefficient used to compute the local-density-based search radius
    // By default, it is set to 0.5.
    private double searchRadius2Coef = 0.5;
    // The alternative coefficient is used for cells that are the direct result of a
    // a splitting event. Indeed, in that case, the two daughter cells can be very close to
    // each other leading to a very small local-density search radius thus the need to
    // define an alternative, higher, coefficient.
    private double alternativeSearchRadius2Coef;
    // The number of frames where a merging cell is accepted by the algorithm. If a merging
    // cell's age exceeds mergingLife, it is deleted.
    private int mergingLife;
    // The number of frames where a fake cell is accepted by the algorithm. If a fake cell's
    // age exceeds fakeLife, it is deleted.
    private int fakeLife;
    // To compute the motion-based search radius for every cell, the algorithm considers its
    // displacements' standard deviation on a defined number of frames in which the cell has
    // been detected. To do so, the spot has to be present for a minimal period of time
    // (thus the minAgeRadius). Likewise, after a certain number of frames where it has been
    // detected (maxAgeRadius1), there is no need to consider the older spots because their
    // displacements become irrelevant.
    private int minAgeRadius1;
    private int maxAgeRadius1;
    // To compute the local-density-based search radius for every cell, the algorithm
    // considers the distances to its nearest neighbours in a defined number of frames in
    // which the cell has been detected. This number is the minimum between the spot's age
    // and the following parameter: the spot's maximum age to compute searchRadius2
    private int maxAgeRadius2;

    /**
     * Constructor: instantiates an empty graph and sets the different fields to
     * a default value
     */
    public LineageTree() {

        // Creates an empty graph 
        graph = new SimpleWeightedGraph(Edge.class);

        leafs = new ArrayList();
        roots = new ArrayList();

        addedSpots = new ArrayList();
        deletedSpots = new ArrayList();

        nbRealEdges = 0;

        // No frame has been processed so the level is set to -1
        // The level refers to the index of the last frame processed
        level = -1;

        mergingToCloned = new HashMap();
        clonedToMerging = new HashMap();

    }

    /**
     * @return the generated graph
     */
    public SimpleWeightedGraph<Spot, Edge> getGraph() {

        return graph;

    }

    /**
     * @return the spots in the frame (t) that have already been linked after
     * the execution of the first step of the algorithm
     */
    public ArrayList<Spot> getLinked1() {

        return linked1;

    }

    /**
     * @return the spots in the frame (t+1) that have already been linked after
     * the execution of the first step of the algorithm
     */
    public ArrayList<Spot> getLinked2() {

        return linked2;

    }

    /**
     * @return the spots in the frame (t) that have not been linked after the
     * execution of the first step of the algorithm
     */
    public ArrayList<Spot> getUnlinked1() {

        return unlinked1;

    }

    /**
     * @return the spots in the frame (t+1) that have not been linked after the
     * execution of the first step of the algorithm
     */
    public ArrayList<Spot> getUnlinked2() {

        return unlinked2;

    }

    /**
     * @return the spots in the frame (t+1) to which the spots in linked1 have
     * been linked after the execution of the first step of the algorithm
     */
    public ArrayList<Spot> getLinked1Children() {

        return linked1Children;

    }

    /**
     * @return the spots in the frame (t) to which the spots in linked2 have
     * been linked after the execution of the first step of the algorithm
     */
    public ArrayList<Spot> getLinked2Parents() {

        return linked2Parents;

    }

    /**
     * @return the current level of the graph
     */
    public int getLevel() {

        return level;

    }

    /**
     * @return the spots that don't have a parent (the spots starting the
     * tracks)
     */
    public ArrayList<Spot> getRoots() {

        return this.roots;

    }

    /**
     * Sets the time interval between two consecutive frames to the entered
     * value
     *
     * @param dt: the duration (in minutes) between two consecutive frames
     */
    public void setTimeInterval(double dt) {

        timeInterval = dt;

    }

    /**
     * Sets the upper limit of every spot's search radius to the entered value
     *
     * @param limit: the upper limit for every spot's search radius
     */
    public void setSearchRadiusUpperLimit(double limit) {

        searchRadiusUpperLimit = limit;

    }

    /**
     * Sets the local-density-based search radius' coefficient to the entered
     * value
     *
     * @param coef: the coefficient considered to compute the spot's
     *              local-density-based search radius
     */
    public void setSearchRadius2Coef(double coef) {

        this.searchRadius2Coef = coef;

    }

    /**
     * Sets the local-density-based search radius' alternative coefficient to
     * the entered value
     *
     * @param coef: the alternative coefficient considered to compute the spot's
     *              local- density-based search radius when the spot is the direct daughter
     *              of a splitting cell
     */
    public void setAlternativeCoef(double alternativeSearchRadius2Coef) {

        this.alternativeSearchRadius2Coef = alternativeSearchRadius2Coef;

    }

    /**
     * Sets the merging life to the entered value
     *
     * @param mergingLife: the maximal age possible for a merging spot
     */
    public void setMergingLife(int mergingLife) {

        this.mergingLife = mergingLife;

    }

    /**
     * Sets the fake life to the entered value
     *
     * @param fakeLife : the maximal age possible for a fake spot
     */
    public void setFakeLife(int fakeLife) {
        this.fakeLife = fakeLife;
    }

    /**
     * Sets the minimal age required to compute the motion-based search radius
     * to the entered value
     *
     * @param minAge: the minimal age required to compute the motion-based
     *                search radius
     */
    public void setMinAgeRadius1(int minAge) {

        minAgeRadius1 = minAge;

    }

    /**
     * Sets the maximal number of frames used to compute the motion-based search
     * radius to the entered value
     *
     * @param maxAge: the maximal number of frames used to compute the
     *                motion-based search radius
     */
    public void setMaxAgeRadius1(int maxAge) {

        maxAgeRadius1 = maxAge;

    }

    /**
     * Sets the maximal number of frames used to compute the local-density-based
     * search radius to the entered value
     *
     * @param maxAge: the maximal number of frames used to compute the
     *                local-density-based search radius
     */
    public void setMaxAgeRadius2(int maxAge) {

        maxAgeRadius2 = maxAge;

    }

    /**
     * @return true if the graph is empty
     */
    public boolean isEmpty() {

        return leafs.isEmpty();

    }

    /**
     * This method realises a real copy of the list entered
     *
     * @param spots: the list to be copied
     * @return the copied list
     */
    private ArrayList<Spot> copyListSpots(ArrayList<Spot> spots) {

        // Creates an empty list
        ArrayList<Spot> res = new ArrayList<Spot>();

        // Loop through the elements of the list to copy
        for (Spot spot : spots) {
            // add every spot found in the list to copy
            res.add(spot);
        }

        return res;
    }

    /**
     * Initialises the empty graph with the entered frame
     *
     * @param frame: the list of objects retrieved from the considered segmented
     *               image
     */
    public void init(Objects3DPopulation frame) {
        // Copies the first spots added in the list of the roots of the graph
        roots = copyListSpots(addFrame(frame));
    }

    /**
     * Initialises the empty graph with the entered frame
     *
     * @param frame:   the list of objects retrieved from the considered segmented
     *                 image
     * @param rawImage
     */
    public void init(Objects3DPopulation frame, ImageHandler rawImage) {
        // Copies the first spots added in the list of the roots of the graph
        roots = copyListSpots(addFrame(frame, rawImage));
    }

    /**
     * Add the entered frame to the graph
     *
     * @param frame: the list of objects retrieved from the segmented image
     * @return the list of spots added to the graph
     */
    public ArrayList<Spot> addFrame(Objects3DPopulation frame) {
        // Creates an empty list of spots
        ArrayList<Spot> res = new ArrayList<Spot>();
        // Loop through the 3D objects in the frame
        for (int i = 0; i < frame.getNbObjects(); i++) {
            // for every object in the frame,
            Object3D tmp = frame.getObject(i);
            // we compute its distance to its closest neighbour. If it does not have a 
            // neighbour (the number of the cells in the segmented image is 1), then the
            // distance to its closest neighbour is set to the upper limit of every spot's 
            // search radius
            double dist = searchRadiusUpperLimit;
            if (frame.closestCenter(tmp, 0) != null) {
                dist = tmp.distCenterUnit(frame.closestCenter(tmp, 0));
            }
            // Create a new spot from the detected 3D object
            Spot spot = new Spot(tmp, nbCreatedSpots, level + 1, dist, timeInterval);
            // Increment the number of created spots
            nbCreatedSpots++;
            // Add the created spot to the graph
            graph.addVertex(spot);
            // Add the spot to the list to be returned
            res.add(spot);
            // Add the spot to the leafs of the tree
            leafs.add(spot);
        }

        // Increments the level of the graph (the index of the last frame processed)
        level++;

        return res;
    }

    /**
     * Add the entered frame to the graph
     *
     * @param frame:   the list of objects retrieved from the segmented image
     * @param rawImage : the raw image to compute intensity-based features
     * @return the list of spots added to the graph
     */
    public ArrayList<Spot> addFrame(Objects3DPopulation frame, ImageHandler rawImage) {

        // Creates an empty list of spots
        ArrayList<Spot> res = new ArrayList<Spot>();

        // Loop through the 3D objects in the frame
        for (int i = 0; i < frame.getNbObjects(); i++) {

            // for every object in the frame,
            Object3D tmp = frame.getObject(i);

            // we compute its distance to its closest neighbour. If it does not have a 
            // neighbour (the number of the cells in the segmented image is 1), then the
            // distance to its closest neighbour is set to the upper limit of every spot's 
            // search radius
            double dist = searchRadiusUpperLimit;

            if (frame.closestCenter(tmp, 0) != null) {
                dist = tmp.distCenterUnit(frame.closestCenter(tmp, 0));
            }

            // Create a new spot from the detected 3D object
            Spot spot = new Spot(tmp, nbCreatedSpots, level + 1, dist, timeInterval);
            // compute intensity based
            if (rawImage != null) {
                spot.setMaxI(tmp.getPixMaxValue(rawImage));
                spot.setMeanI(tmp.getPixMeanValue(rawImage));
                spot.setMedianI(tmp.getPixMedianValue(rawImage));
                spot.setMinI(tmp.getPixMinValue(rawImage));
            }

            // Increment the number of created spots
            nbCreatedSpots++;

            // Add the created spot to the graph
            graph.addVertex(spot);

            // Add the spot to the list to be returned
            res.add(spot);

            // Add the spot to the leafs of the tree
            leafs.add(spot);

        }

        // Increments the level of the graph (the index of the last frame processed)
        level++;

        return res;

    }

    /**
     * Extracts the child-less spots of the last frame processed
     *
     * @return : the list of the child-less spots that were added after the
     * processing of the last frame
     */
    public ArrayList<Spot> leafsInLastFrame() {

        // Creates an empty list od spots
        ArrayList<Spot> res = new ArrayList<Spot>();

        // Loop through the child-less spots in the graph
        for (int i = 0; i < leafs.size(); i++) {
            // If the considered spot belongs to the last frame processed
            if (leafs.get(i).getFrame() == level) {
                // add the considered spot to the list to be returned
                res.add(leafs.get(i));
            }
        }

        return res;
    }

    /**
     * Adds an edge between the two spots in the graph
     *
     * @param source : the spot in the frame (t)
     * @param target : the spot in the frame (t+1)
     * @param cost   : the cost for linking the spot source to the spot target
     * @param fake   : true if the edge is fake (for example, when the spot target
     *               is a cloned spot)
     */
    private void addEdge(Spot source, Spot target, double cost, boolean fake) {

        // Add an edge between the spot source and  the spot target in the graph
        Edge edge = graph.addEdge(source, target);

        // Sets the edge's nature (real or fake)
        edge.fake(fake);

        // Sets the edge's linking cost
        edge.setLinkCost(cost);

        // Sets the edge's spots
        edge.setSpotSourceID(source.getID());
        edge.setSpotTargetID(target.getID());

        // Compute the displacement of the cell along the three axes
        double dx = source.getPosX() - target.getPosX();
        double dy = source.getPosY() - target.getPosY();
        double dz = source.getPosZ() - target.getPosZ();

        // Compute the time interval separating the two spots
        double dt = source.getPosT() - target.getPosT();

        // Compute the distance between the two spots (the cell's displacement)
        double d = Math.sqrt(dx * dx + dy * dy + dz * dz);

        // Compute the cell's velocity
        double v = d / Math.abs(dt);

        // Sets the edge's displacement
        edge.setDisplacement(d);

        // Sets the edge's velocity
        edge.setVelocity(v);

        // Update the mean and the standard deviation values of all the cells' displacement
        updateDisplacement(edge);

        if (roots.contains(target)) {
            roots.remove(target);
        }

        if (leafs.contains(source)) {

            leafs.remove(source);
        }

    }

    /**
     * Updates the mean and the standard deviation values of all the cells'
     * displacement every time an edge is added to the graph
     *
     * @param edge: the edge that has just been added to the graph
     */
    private void updateDisplacement(Edge edge) {

        // If the edge is real (if it is fake, there is no real cell's displacement)
        if (!edge.isFake()) {

            // Increment the number of real edges
            nbRealEdges++;

            // Compute the new mean displacement
            double newMeanDisplacement = currentMeanDisplacement
                    + (edge.getDisplacement() - currentMeanDisplacement) / nbRealEdges;

            // Compute the second moment of the cells' displacement
            currentMoment2Displacement = currentMoment2Displacement
                    + (edge.getDisplacement() - currentMeanDisplacement)
                    * (edge.getDisplacement() - newMeanDisplacement);

            // Update the current mean displacement
            currentMeanDisplacement = newMeanDisplacement;

        }

        /* The algorithm used to compute the mean and the standard deviation values of 
         * the cells' displacement is explained in: 
         * en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Online_algorithm
         */
    }

    /**
     * Removes a real edge from the graph and updates the displacements'
     * statistics accordingly
     *
     * @param edge : the real edge to be removed
     * @return the linking cost of the removed edge
     */
    private double removeRealEdge(Edge edge) {

        double cost = 0;

        if (!edge.isFake()) {

            // Retrieve the cost of the edge to be removed
            cost = edge.getLinkCost();

            // Retrieve the value of the cell's displacement related to the considered edge
            double val = edge.getDisplacement();

            // Update the cells' displacements statistics
            currentMeanDisplacement = (currentMeanDisplacement * nbRealEdges - val) / (nbRealEdges - 1);
            currentMoment2Displacement = currentMoment2Displacement - (val - currentMeanDisplacement) * (val - currentMeanDisplacement);

            // Update the number of real edges
            nbRealEdges--;

            // Remove the edge from the graph
            graph.removeEdge(edge);

        }

        return cost;

    }

    /**
     * Removes a real edge from the graph and updates the displacements'
     * statistics accordingly
     *
     * @param source : the spot source of the edge to be removed
     * @param target : the spot target of the edge to be removed
     * @return the linking cost of the removed edge
     */
    private double removeRealEdge(Spot source, Spot target) {

        // Retrieve the edge between the spot source and the spot target and remove it
        return removeRealEdge(graph.getEdge(source, target));

    }

    /**
     * Looks for the children of the considered spot
     *
     * @param spot : the spot whose children are sought
     * @return the considered spot's children
     */
    public ArrayList<Spot> getChildren(Spot spot) {

        // Create the list that will contain the considered spot's children
        ArrayList<Spot> children = new ArrayList<Spot>();

        // Retrieve the edges touching the considered spot in the graph 
        Edge[] edges = (Edge[]) graph.edgesOf(spot).toArray(new Edge[0]);

        // Loop through the edges touching the spot
        for (int i = 0; i < edges.length; i++) {

            // For the edges whose the source is the considered spot
            if (graph.getEdgeSource(edges[i]).equals(spot)) {

                // Add their target to the considered spot's children's list
                children.add(graph.getEdgeTarget(edges[i]));
            }

        }

        return children;
    }

    /**
     * Looks for the parent of the considered spot
     *
     * @param spot : the spot whose parent is sought
     * @return the parent of the considered spot
     */
    public Spot getParent(Spot spot) {
        // Initialise the spot's parent 
        Spot parent = null;

        // Retrieve all the edges in the graph touching the considered spot
        if (!graph.containsVertex(spot)) {
            //System.out.println("PARENT " + graph.containsVertex(spot));
            return null;
        }
        Set<Edge> ed = graph.edgesOf(spot);

        if ((ed == null) || (ed.isEmpty())) {
            //System.out.println("PARENT " + " " + ed + " " + graph.containsVertex(spot));
            return null;
        }
        Edge[] edges = (Edge[]) ed.toArray(new Edge[0]);

        // Loop through the edges touching the spot
        for (Edge edge : edges) {
            // For the edges whose the source is the considered spot
            if (graph.getEdgeTarget(edge).equals(spot)) {
                parent = graph.getEdgeSource(edge);
            }
        }

        return parent;
    }

    public Spot getAncestor(Spot spot) {
        Spot parent = getParent(spot);

        if (parent == null) {
            return null;
        }

        if (getChildren(parent).size() == 1) {
            return getAncestor(parent);
        }

        return spot;
    }

    /**
     * Computes the spot's motion-based search radius
     *
     * @param spot: the spot whose motion-based search radius is computed
     */
    private void setSearchRadius1(Spot spot) {

        /*
         * The motion-based search radius is computed as three times the standard deviation
         * of the cell's displacements. If the spot's age is too small (the cell has just
         * been detected), then the standard deviation of its displacements is meaningless
         * and the standard deviation of all the cell's displacements in the graph is used 
         * instead. If this value is still undefined, the default value 0.5 is used.
         * 
         * If the cell has already been detected for a long time, I compute its
         * displacement's standard deviation by considering its positions in the previous 
         * frames. However, I only use a certain number of frames on which the cell has been
         * detected. I assume that after a period of time, the cell's motion changes. This
         * is particularly true for the daughters of a splitting cell. We know that they 
         * drift from their parents in opposite directions at the beginning of their 
         * existence but their motion becomes random after a certain period. Then, we should
         * not consider their first displacements to compute the search radius.
         * 
         */
        // If the spot's age is too small to compute its motion-based search radius
        if (spot.getAge() < minAgeRadius1) {

            // The default value for all the cells' displacements' standard deviation
            double sigma = 0.5;

            // If at least one displacement has been computed (false for the first frame)
            if (nbRealEdges > 0) {

                // Update the value of all the cells' displacements' standard deviation
                sigma = Math.sqrt(currentMoment2Displacement / nbRealEdges);

            }

            // Sets the spot's displacement's standard deviation to the computed value
            spot.setStandardDeviationDisplacement(sigma);
            // Computes the spot's motion-based search radius (3 * sigma)
            spot.setSearchRadius1();

        } // If the spot is old enough to compute its motion-based search radius
        else if (spot.getAge() > minAgeRadius1) {

            // Determine the number of frames used to compute the spot's search radius
            int nb = Math.min(spot.getAge(), maxAgeRadius1);

            // The mean value of the spot's displacement
            double mean = 0;

            // Use a temporary spot
            Spot tmp = spot;

            // The array containing the value of the cell's displacements for the period 
            // considered
            double[] val = new double[nb - 2];

            // for the considered period
            for (int i = 0; i < nb - 2; i++) {

                // Retrieve the parent of the temporary spot
                Spot parent = getParent(tmp);

                // Retrieve the edge linking the temporary spot and its parent
                Edge edge = graph.getEdge(parent, tmp);

                // Store the displacement value in the array
                val[i] = edge.getDisplacement();

                // Update the mean value
                mean = mean + val[i] / (nb - 2);
            }

            // The variance value of the spot's displacements
            double var = 0;

            // for the number of displacements considered
            for (int i = 0; i < nb - 2; i++) {

                // Update the variance value
                var = var + (val[i] - mean) * (val[i] - mean);

            }

            // Update the variance value
            var = var / (nb - 3);

            // Compute the standard deviation
            double sigma = Math.sqrt(var);

            // Sets the spot's related fields
            spot.setMeanDisplacement(mean);
            spot.setStandardDeviationDisplacement(sigma);
            spot.setSearchRadius1();

        }

    }

    /**
     * Computes the spot's local-density-based search radius
     *
     * @param spot: the spot whose local-density-based search radius is computed
     */
    private void setSearchRadius2(Spot spot) {

        /*
         * The local-density-based search radius is computed using the distance from the 
         * spot to its nearest neighbour in the frame. If the spot has been tracked for 
         * some time, it is possible to consider this distance in the previous frames. 
         * However, to keep the computation cost reasonable, we consider at most a fixed
         * number of frames even if the spot had been tracked for longer time.
         * 
         * We consider a higher coefficient (alternativeSearchRadius2Coeff) for the spots
         * that are the direct daughters of splitting spots. Indeed, the sister cells are 
         * very close to each other leading to a very small local-density-based search 
         * radius unless a higher coefficient is considered.
         * 
         */
        // Determine the number of frames used to compute the local-density-based search 
        // radius
        int nb = Math.min(spot.getAge(), maxAgeRadius2);

        // The distance from the spot to its closest object in the frame
        double dist = spot.distanceToClosestObjectInFrame();

        // Use a temporary spot
        Spot tmp = spot;

        // for the considered number of frames
        for (int i = 0; i < nb - 1; i++) {

            if (tmp.getFrame() > spot.getFrame() - nb) {

                // Retrieve the temporary spot's parent
                Spot parent = getParent(tmp);

                if (parent != null) {
                    // Update the sum of distances from the cell's detections to their closest
                    // neighbour
                    dist = dist + parent.distanceToClosestObjectInFrame();

                    // Update the temporary spot
                    tmp = parent;
                }
            }
        }

        // Compute the local-density-based search radius
        spot.setSearchRadius2(searchRadius2Coef * dist / nb);

        // if the spot is the direct result of a real splitting event (i.e the parent is 
        // not a merging spot) ...
        if (spot.isResultOfSplitting() && getParent(spot).getState() != Spot.MERGING) {

            // Use the alternative  coefficient (supposed to be higher than 
            // searchRadius2Coef) to compute the local-density-based search radius
            spot.setSearchRadius2(alternativeSearchRadius2Coef * dist / nb);

        }

    }

    /**
     * Make the associations selected by the Hungarian Algorithm after solving
     * the first step's Linear Assignment Problem written as a matrix containing
     * the costs to link any spot in the frame (t) to any spot in the frame
     * (t+1)
     *
     * @param spots1           : the spots contained in the frame (t)
     * @param spots2           : the spots contained in the frame (t+1)
     * @param assignmentVector : the solution provided by the Hungarian
     *                         Algorithm
     * @param costs:           the matrix storing the costs to link any spot in the frame
     *                         (t) to any spot in the frame (t+1)
     */
    public void addEdgesStep1(ArrayList<Spot> spots1, ArrayList<Spot> spots2, int[] assignmentVector, double[][] costs) {

        // Instantiate the lists that will be used in the second step of the algorithm
        linked1 = new ArrayList<Spot>();
        linked2 = new ArrayList<Spot>();
        unlinked1 = new ArrayList<Spot>();
        unlinked2 = new ArrayList<Spot>();
        linked1Children = new ArrayList<Spot>();
        linked2Parents = new ArrayList<Spot>();

        /* 
         * Let's note n(t) and n(t+1) the number of spots, respectively, in the frame 
         * (t) and (t+1). The assignment vector size is therefore n(t)+ n(t+1).
         * 
         * If we remember the first step's cost matrix's structure :
         *  	    ╔════════════════════════════════════╦══════════════════════╗
         *          ║            objects(t+1)            ║       objects(t)     ║
         *  	    ╠════════════════════════════════════╬══════════════════════╣
         *          ║   1       2    . . .     n(t+1)    ║ 1   2   . . .   n(t) ║
         * ╔════════╬════════════════════════════════════╬══════════════════════╣
         * ║   1    ║ c11      c12   . . .     c1n(t+1)  ║ d   x   . . .    x   ║ 
         * ║   2    ║ c21       x    . . .     c2n(t+1)  ║ x   d   . . .    x   ║
         * ║   .    ║  .        .    .            .      ║ .   .   .        .   ║
         * ║   .    ║  .        .      .          .      ║ .   .     .      .   ║
         * ║   .    ║  .        .        .        .      ║ .   .       .    .   ║
         * ║ n(t)   ║ c1n(t)    x    . . .   cn(t)n(t+1) ║ x   x   . . .    d   ║
         * ╠════════╬════════════════════════════════════╬══════════════════════╣
         * ║   1    ║  d        x    . . .        x      ║ s   s   . . .    s   ║
         * ║   2    ║  x        d    . . .        x      ║ s   x   . . .    x   ║
         * ║   .    ║  .        .    .            .      ║ .   .   .        .   ║
         * ║   .    ║  .        .      .          .      ║ .   .     .      .   ║
         * ║   .    ║  .        .        .        .      ║ .   .       .    .   ║
         * ║ n(t+1) ║  x        x    . . .        d      ║ s   s   . . .    s   ║
         * ╚════════╩════════════════════════════════════╩══════════════════════╝
         *  we see that the assignment vector has the same rows as the cost matrix.
         *  Therefore, every line in the assignment vector models a spot: the first n(t)
         *  spots belong to the frame (t) and the last n(t+1) ones belong to the frame
         *  (t+1). The value contained in every case of the assignment vector models the
         *  spot to which they are linked. I chose this value to be the index of the 
         *  cost matrix's correspondent column.
         *  
         *  Let i < n(t) and j = assignmentVector[i] :
         *  	- if j < n(t+1) then the spot i in the frame (t) is linked to the spot
         *  						  j in the frame (t+1)
         *   	- if j >= n(t) then the spot i in the frame (t) remains unlinked
         *  
         *  Let i >= n(t) and j = assignmentVector[i] with j < n(t+1). It means that the
         *  spot j in the frame (t+1) remains unlinked.
         *  
         */
        // Loop through the spots contained in the frame (t)
        for (int i = 0; i < spots1.size(); i++) {
            // Retrieve the index to which the Hungarian Algorithm assigned the current spot
            int j = assignmentVector[i];
            // If the Hungarian Algorithm assigned the current spot to a spot in the frame 
            // (t+1)
            if (j < spots2.size()) {
                // Add the real edge between the two spots
                addEdge(spots1.get(i), spots2.get(j), costs[i][j], false);
                // Add the current spot to linked  spots in the frame (t)
                linked1.add(spots1.get(i));
                // Mark the spot in the frame (t+1) as the child of the current spot
                linked1Children.add(spots2.get(j));
                // Add the spot in the frame (t+1) to the linked spots of the frame (t+1)
                linked2.add(spots2.get(j));
                // Mark the current spot as the parent of the spot in the frame (t+1)
                linked2Parents.add(spots1.get(i));
            } // if the Hungarian Algorithm keeps the current spot unlinked
            else {
                // Add the current spot to the unlinked spots of the frame (t)
                unlinked1.add(spots1.get(i));
            }
        }

        // Loop through the spots contained in the frame (t+1)
        for (int i = spots1.size(); i < spots1.size() + spots2.size(); i++) {
            // Retrieve the index to which the Hungarian Algorithm assigned the current spot
            int j = assignmentVector[i];
            // If the current spot is assigned to itself
            if (j == i - spots1.size()) {
                // Add the current spot to the unlinked spots in the frame (t+1)
                unlinked2.add(spots2.get(j));
            }
        }
    }

    /**
     * Make the associations selected by the Hungarian Algorithm after solving
     * the second step's Linear Assignment Problem
     *
     * @param assignmentVector : the solution provided by the Hungarian
     *                         Algorithm
     * @param costs:           the matrix storing the costs for difficult links between
     *                         spots in the frame (t) and the spots in the frame (t+1)
     */
    public void addEdgesStep2(int[] assignmentVector, double[][] costs) {

        // The number of the unlinked spots in the frame (t) after the first step
        int nbUnlinked1 = unlinked1.size();
        // The number of the unlinked spots in the frame (t+1) after the first step
        int nbUnlinked2 = unlinked2.size();
        // The number of the linked spots in the frame (t) after the first step
        int nbLinked1 = linked1.size();
        // The number of the linked spots in the frame (t+1) after the first step
        int nbLinked2 = linked2.size();

        /* 
         * The structure of the second step's cost matrix is as follows: 
         *	                ╔═══════════════╦══════════════╦═════════════╦════════════════╗
         *    	            ║ Unlinked(t+1) ║   Linked(t)  ║ Linked(t+1) ║   Unlinked(t)  ║
         *	╔═══════════════╬═══════════════╬══════════════╬═════════════╬════════════════╣
         *	║  Unlinked(t)  ║  Gap Closing  ║      X       ║ Merging     ║ No Linking     ║
         *	╠═══════════════╬═══════════════╬══════════════╬═════════════╬════════════════╣
         *	║   Linked(t)   ║ Splitting     ║ No Linking   ║      X      ║       X        ║
         *	╠═══════════════╬═══════════════╬══════════════╬═════════════╬════════════════╣
         *	║  Linked(t+1)  ║       X       ║      X       ║ No Linking  ║ t(Merging)     ║
         *	╠═══════════════╬═══════════════╬══════════════╬═════════════╬════════════════╣
         *	║ Unlinked(t+1) ║ No Linking    ║ t(Splitting) ║      X      ║ t(Gap Closing) ║
         *	╚═══════════════╩═══════════════╩══════════════╩═════════════╩════════════════╝
         *  
         *  Like the columns in the cost matrix, every line in the assignment vector models 
         *  a spot in one of the unlinked1, linked1, linked2 and unlinked2 lists. The value 
         *  stored in the vector is the index of the spot to which the considered spot is 
         *  linked. However, this index may also refer to the considered spot itself in which
         *  case the spot remains unlinked.
         *  
         *  In the following, we will discuss the different cases.
         */
        // Loop through the unlinked spots in the frame (t)
        for (int i = 0; i < nbUnlinked1; i++) {
            System.out.println("testing U1 " + unlinked1.get(i));
            // Retrieve the index to which the unlinked spot is assigned
            int j = assignmentVector[i];
            // if the spot in the frame (t+1) is also unlinked (Gap Closing)
            if (j < nbUnlinked2) {
                // Add a real edge between the two considered spots
                addEdge(unlinked1.get(i), unlinked2.get(j), costs[i][j], false);
                System.out.println("add edge " + unlinked1.get(i) + " " + unlinked2.get(j));
            } // if the spot in the frame (t+1) has already been linked in the first step
            // (Merging)
            else if (nbUnlinked2 + nbLinked1 <= j && j < nbUnlinked2 + nbLinked1 + nbLinked2) {
                // Mark the spot in the frame (t+1) as a merging spot
                linked2.get(j - nbUnlinked2 - nbLinked1).setState(Spot.MERGING);
                // Create a cloned spot in the frame (t+1)
                Spot newSpot = new Spot(linked2.get(j - nbUnlinked2 - nbLinked1), nbCreatedSpots);
                // Increment the number of created spots
                nbCreatedSpots++;

                // Set the fields of the cloned spot
                newSpot.setFrame(linked2.get(j - nbUnlinked2 - nbLinked1).getFrame());
                newSpot.setPosT(timeInterval);
                newSpot.setState(Spot.CLONED);
                newSpot.setAge(1);

                // Add the new spot to the graph
                graph.addVertex(newSpot);

                // Update the tree lineage's leafs
                leafs.add(newSpot);

                // Performs the mapping the merging spot and the cloned one
                this.mergingToCloned.put(linked2.get(j - nbUnlinked2 - nbLinked1), newSpot);
                this.clonedToMerging.put(newSpot, linked2.get(j - nbUnlinked2 - nbLinked1));

                // Add a fake edge between the considered spot in the frame (t) and the 
                // cloned spot
                addEdge(unlinked1.get(i), newSpot, costs[i][j], true);
                System.out.println("add merge " + unlinked1.get(i) + " " + newSpot);
            }
        }

        // Loop through the linked spots in the frame (t)
        for (int i = nbUnlinked1; i < nbUnlinked1 + nbLinked1; i++) {
            // Retrieve the index to which the linked spot is assigned
            int j = assignmentVector[i];
            System.out.println("testing split " + linked1.get(i - nbUnlinked1) + " " + j + " " + costs[i][j]);
            // if the spot in the frame (t+1) is unlinked (Splitting)
            if (j < nbUnlinked2) {
                // Add a real edge between the two spots
                addEdge(linked1.get(i - nbUnlinked1), unlinked2.get(j), costs[i][j], false);
                System.out.println("add split " + linked1.get(i - nbUnlinked1) + " " + unlinked2.get(j));
                // Mark the daughters as direct result of splitting
                unlinked2.get(j).setResultOfSplitting(true);
                linked1Children.get(i - nbUnlinked1).setResultOfSplitting(true);
            }
        }

        // Loop through the unlinked spots in the frame (t+1) 
        for (int i = nbUnlinked1 + nbLinked1 + nbLinked2; i < nbUnlinked1 + nbUnlinked2 + nbLinked1 + nbLinked2; i++) {
            System.out.println("testing U2 " + unlinked2.get(i - nbUnlinked1 - nbLinked1 - nbLinked2) + " " + assignmentVector[i]);
            // Retrieve the index to which the unlinked spot is assigned
            int j = assignmentVector[i];
            // If the spot is assigned to itself
            if (j == i - nbUnlinked1 - nbLinked1 - nbLinked2) {
                // Add the spots to the list of roots (a new track is starting)
                roots.add(unlinked2.get(j));
                System.out.println("add new " + unlinked2.get(j));
            }
        }
    }

    /**
     * Analyses the associations made after the execution of the first two steps
     * and corrects them if possible
     */
    public void updateSpots() {
        // Retrieve the unlinked spots in the frame (t): end of tracks
        ArrayList<Spot> unlinkedOldLeafs = leafsInBeforeLastFrame();

        // Loop through the unlinked spots in the frame (t)
        for (Spot tmp : unlinkedOldLeafs) {
            // Analyse the current spot (end of track) 
            analyzeEndTrack(tmp);
        }

        // Retrieve the new leafs of the tree
        ArrayList<Spot> newLeafs = leafsInLastFrame();

        // Extract the unlinked spots in the new leafs: start of tracks
        ArrayList<Spot> unlinkedNewLeafs = extractUnlinked(newLeafs);

        // Loop through the unlinked spots in the frame (t+1)
        for (Spot tmp : unlinkedNewLeafs) {
            // Analyse the current spot (start of track)
            analyzeStartTrack(tmp);
        }

        // Loop through the new leafs of the tree
        for (Spot tmp : newLeafs) {
            // Update the spot (compute its age and its search radii)
            updateSpot(tmp);
        }

        // Extract the merging spots in the new leafs
        ArrayList<Spot> mergingSpotsInNewLeafs = extractMergingSpots(newLeafs);

        // Loop through the merging spots in the new leafs
        for (Spot tmp : mergingSpotsInNewLeafs) {
            // Analyse the spot
            analyzeMergingSpot(tmp);
        }

        // Extract the default spots in the new leafs
        ArrayList<Spot> normalSpots = extractNormalSpots(leafsInLastFrame());

        // Loop through the default spots in the new leafs
        for (Spot tmp : normalSpots) {
            // Analyse the spot
            analyzeNormalSpot(tmp);
        } /*
         * The order chosen to perform the spots analysis is important. I started with the
         * end of tracks because the processing consists in adding a fake cell in the
         * following frame. This cell is now part of the new leafs and should be processed
         * accordingly. That's why the analysis of the new leafs should be done after the
         * end of tracks.
         *
         * It is also important to update spots before processing the merging ones, since 
         * the processing includes an exchange between merging and cloned spots.
         *
         * Finally, I analyse the normal spots after the merging ones because the merging 
         * analysis may lead to update some spots' state. That's why I call one more time
         * leafsInLastFrame after the merging analysis instead of using newLeafs to extract
         * the default spots.
         */

    }

    /**
     * @return the spots leading to an end of tracks after the execution of the
     * algorithm's first and second steps
     */
    private ArrayList<Spot> leafsInBeforeLastFrame() {

        // Create the list where the spots consisting in an end of track will be stored
        ArrayList<Spot> unlinked = new ArrayList<Spot>();

        // Loop through all the child-less spots
        for (Spot tmp : leafs) {
            // If the current child-less spot belongs to the frame (t) then it is an end of
            // track because the associations between the spots in the frame (t) and those 
            // in the frame (t+1) have already been made in the first and second steps of the
            // algorithm
            if (tmp.getFrame() == level - 1) {
                unlinked.add(tmp);
            }
        }

        return unlinked;

    }

    /**
     * Extracts the orphan spots recently-added to the graph as they are the
     * spots beginning new tracks
     *
     * @param newLeafs : the spots recently-added to the graph (the spots in the
     *                 frame (t+1))
     * @return the list of the orphan spots in the frame (t+1)
     */
    private ArrayList<Spot> extractUnlinked(ArrayList<Spot> newLeafs) {

        // Create the list where the orphan spots in the frame (t+1) will be stored
        ArrayList<Spot> unlinked = new ArrayList<Spot>();

        // We don't consider the first spots added in the algorithm because they naturally
        // begin new tracks and do not need any further processing
        if (level != 0) {

            // Loop through the spots in the frame (t+1)
            for (Spot tmp : newLeafs) {
                // if the spot is not linked, then it does not have a parent
                if (graph.edgesOf(tmp).isEmpty()) {

                    unlinked.add(tmp);

                }
            }

        }

        return unlinked;

    }

    /**
     * Extracts the merging spots recently-added to the graph
     *
     * @param newLeafs : the spots in the frame (t+1) who were recently added to
     *                 the graph
     * @return the list of the merging spots in the frame (t+1)
     */
    private ArrayList<Spot> extractMergingSpots(ArrayList<Spot> newLeafs) {

        // Create the list where the merging spots of the frame (t+1) will be stored
        ArrayList<Spot> merging = new ArrayList<Spot>();

        // Loop through the spots in the frame (t+1)
        for (Spot tmp : newLeafs) {
            // If it is a merging spot, it is added to the list to be returned
            if (tmp.getState() == Spot.MERGING) {
                merging.add(tmp);
                System.out.println("Merging detected " + tmp);
            }
        }

        return merging;
    }

    /**
     * Extracts the default spots recently-added to the graph
     *
     * @param leafsInLastFrame : the spots in the frame (t+1) who were recently
     *                         added to the graph
     * @return the list of the default spots in the frame (t+1)
     */
    private ArrayList<Spot> extractNormalSpots(ArrayList<Spot> leafsInLastFrame) {

        // Create the list where the default spots in the frame (t+1) will be stored
        ArrayList<Spot> normalSpots = new ArrayList<Spot>();

        // Loop through the spots in the frame (t+1)
        for (Spot leafsInLastFrame1 : leafsInLastFrame) {
            // if the current spot in the frame (t+1) is default then it is added to the list
            // to be returned
            if (leafsInLastFrame1.getState() == Spot.DEFAULT) {
                normalSpots.add(leafsInLastFrame1);
            }
        }

        return normalSpots;

    }

    /**
     * Analyses a spot leading to an end of track (a child-less spot in the
     * frame (t) after the execution of the two first steps of the algorithm).
     *
     * @param tmp : the spot leading to an end of track
     */
    private void analyzeEndTrack(Spot tmp) {
        /*
         * Three reasons may cause the ending of a track : the cell dies, the cell leaves 
         * the field of view and the cell was not detected in the segmentation algorithm (a
         * missed detection). 
         * 
         * When a spot in the frame (t) remains unlinked after the execution of the two 
         * first steps of the algorithm, we need to make sure that it is not because of a 
         * missed detection. 
         * 
         * To do so, we add a fake cell in the frame (t+1) located in the same position as 
         * the end-of-track spot and we link them together. 
         * 
         * When the algorithm processes the next step, two cases may occur for our added fake
         * cell: either it is linked to a real spot and therefore we are sure our end-of-
         * track spot was caused by a missed detection or it becomes at its turn an end-of-
         * track fake spot. 
         * 
         * For the second case, the chosen solution consists in keeping creating fake cells until
         * its age reaches the maximal number of frames allowed for fake cells. If so, we 
         * remove all the fake cells created and conclude in a real end of 
         * track caused by the cell's death or the cell's leaving the field of view.
         */
        // If the spot is fake (it has been added by the algorithm and not really detected 
        // in a segmented image)
        if (tmp.getState() == Spot.FAKE) {
            // if the fake spot age is smaller than the maximum number of frames allowed for
            // fake spots
            if (tmp.getAge() < fakeLife) {
                // Add a fake cell in the following frame
                addAFakeCell(tmp);
            } // If the fake spot has been present for too long (its age reaches the number of
            // frames allowed for fake spots)
            else {
                // Remove the fake spot and all its fake ancestors
                removeFakeCells(tmp);
            }
        } // the spot leading to an end of track is not fake
        else {
            // Add a fake cell in the following frame
            addAFakeCell(tmp);
        }
    }

    /**
     * Adds a fake cell at the same position as the considered spot in the
     * following frame
     *
     * @param tmp : the child-less spot which will be linked to a fake cell
     */
    private void addAFakeCell(Spot tmp) {

        // Create a fake cell at the same position as the considered spot
        Spot spot = new Spot(tmp, nbCreatedSpots);
        // Increment the number of created spots
        nbCreatedSpots++;

        // The fake cell belongs to the frame after the considered spot's
        spot.setFrame(tmp.getFrame() + 1);

        // Sets the rest of the fake cell's attributes
        spot.setPosT(timeInterval);
        spot.setAge(tmp.getAge() + 1);
        spot.setState(Spot.FAKE);

        // Add the fake cell to the graph
        graph.addVertex(spot);

        // Add the fake cell created to the list of the child-less spots
        leafs.add(spot);

        // Link the considered spot to the fake cell using a fake edge
        addEdge(tmp, spot, 0, true);

    }

    /**
     * Remove the fake cell form the graph
     *
     * @param tmp : the fake cell to be removed
     */
    private void removeFakeCell(Spot tmp) {

        // Checks if the entered cell is fake
        if (tmp.getState() == Spot.FAKE) {

            // if the fake cell is child-less, remove it from the child-less spots' list
            if (leafs.contains(tmp)) {
                leafs.remove(tmp);
            }

            // if the fake cell is orphan (the cell is the start of a new track and is 
            // considered as a false positive)
            if (roots.contains(tmp)) {

                // Remove the cell from the orphan spots' list
                roots.remove(tmp);

                // Add the cell to the list of the spots deleted by the tracking algorithm 
                // even if they were truly detected in the segmentation algorithm
                deletedSpots.add(tmp);

            } // the fake cell has a parent
            else {

                // Retrieve the parent of the fake cells
                Spot parent = getParent(tmp);

                // Remove the fake edge between the fake cell and its parent
                graph.removeEdge(parent, tmp);

                // Add the parent to the child-less spots' list
                leafs.add(parent);

            }

            // Remove the fake cell from the graph
            graph.removeVertex(tmp);

        }

    }

    /**
     * Remove the entered fake cell and its fake ancestors from the graph
     *
     * @param tmp : the fake cell
     */
    private void removeFakeCells(Spot tmp) {
        // Checks that the considered spot is fake 
        if (tmp.getState() == Spot.FAKE) {
            // if the considered spot is orphan
            if (roots.contains(tmp)) {
                // the only cell to be removed is the entered cell
                removeFakeCell(tmp);
            } // the fake cell has a parent
            else {
                // Retrieve the parent of the fake cell
                Spot parent = getParent(tmp);

                // if the parent is also a fake cell
                if ((parent != null) && (parent.getState() == Spot.FAKE)) {
                    // Remove the current fake cell
                    removeFakeCell(tmp);

                    // Remove the parent and all its fake ancestors
                    removeFakeCells(parent);

                } // the parent of the fake cell is not fake
                else {
                    // Remove the current fake cell
                    removeFakeCell(tmp);
                }
            }
        }
    }

    /**
     * Analyses a spot leading to a start of track (an orphan spot in the frame
     * (t+1) after the execution of the first two steps of the algorithm)
     *
     * @param tmp : the first spot in the new track
     */
    private void analyzeStartTrack(Spot tmp) {


        /*
         * Two reasons may lead to the beginning of a new track: a new cell enters the field
         * of view or a cell is detected even if it is not present (a false positive).
         * 
         * To make sure the new cell is not a false positive, we set it as a fake cell. If it
         * is linked in the following frames (before its age reaches fakeLife), then it was
         * indeed a new track and we set its state back to default. However, if it remains 
         * unlinked for fakeLife iterations, then the algorithm concludes to a false positive
         * and deletes the cell.
         */
        // Mark the first spot of the new track as fake
        tmp.setState(Spot.FAKE);

    }

    /**
     * Compute the spot's age and search radii. If applicable, exchange the
     * merging-cloned associations.
     *
     * @param tmp : the spot to be updated
     */
    private void updateSpot(Spot tmp) {
        /*
         * This processing aims to ensure that a merging spot can not have a cloned 
         * parent and a cloned spot can not have a merging parent via the method
         * correctMergingClonedAssociations.
         */
        // if the spot is merging
        if (tmp.getState() == Spot.MERGING) {
            System.out.println("Spot " + tmp + "merge");
            // Exchange merging-cloned associations if applicable
            correctMergingClonedAssociations(tmp);

            // test
            Spot parent = getParent(tmp);

            if (parent.isResultOfSplitting()) {
                System.out.println("Spot " + parent + " split and merge");
            }
        }
        /*
         * Set the spot's age
         */
        // if the spot has a parent
        if (!roots.contains(tmp)) {

            // Retrieve the spot's parent
            // System.out.println("PB spot " + tmp);
            Spot parent = getParent(tmp);

            // if the spot is not the direct result of splitting
            if (getChildren(parent).size() == 1) {

                // the age of this spot is the age of its parent incremented
                tmp.setAge(parent.getAge() + 1);
            }

            // if the parent's state is default and the spot's state is merging, cloned or
            // fake, then the spot's age is set back to 1.
            if (parent.getState() == Spot.DEFAULT
                    && (tmp.getState() == Spot.MERGING || tmp.getState() == Spot.CLONED
                    || tmp.getState() == Spot.FAKE)) {

                tmp.setAge(1);
            }
        }


        /* 
         * Sets the spot's search radii
         */
        setSearchRadius1(tmp);
        setSearchRadius2(tmp);
    }

    /**
     * Checks if the merging spot is the result of a false merging event. A
     * false merging occurs when a merging spot and its clone merge in the
     * following frame.
     *
     * @param merging       : the merging spot
     * @param mergingParent : the parent of the merging spot
     * @param cloned:       the clone of the merging spot
     * @param clonedParent: the parent of the clone of the merging spot
     * @return true if the merging spot is the result of a false merging event
     */
    private boolean isFakeMergingSpot(Spot merging, Spot mergingParent, Spot cloned, Spot clonedParent) {
        /*
         * For every merging spot, the method correctMergingClonedAssociations has been 
         * called. Therefore, the parent of the merging spot can not be a cloned spot. 
         * Knowing this result, a false merging event is detected whenever the parent of 
         * the merging spot is also merging and if the parent's clone happens to be the 
         * parent of the merging spot's clone. 
         */
        return mergingParent.getState() == Spot.MERGING && mergingToCloned.get(mergingParent).equals(clonedParent);

    }

    /**
     * Exchange the merging and cloned parents so that a merging spot can not
     * have a cloned parent and a cloned spot can not have a merging parent.
     *
     * @param merging: the merging spot that will be checked and corrected if
     *                 needed
     */
    private void correctMergingClonedAssociations(Spot merging) {

        // TODO schemas pour expliquer les cas 
        // Retrieve the parent of the merging spot
        Spot mergingParent = getParent(merging);

        // Retrieve the clone of the merging spot
        Spot cloned = mergingToCloned.get(merging);

        // Retrieve the parent of the clone of the merging spot
        Spot clonedParent = getParent(cloned);
        Spot clonedAncestor = getAncestor(cloned);

        // If the merging spot's parent is a cloned spot
        if (mergingParent.getState() == Spot.CLONED) {
            Spot realParent = clonedToMerging.get(mergingParent);

            // if the merging spot and its cloned have respectively a cloned and a merging 
            // spots as parents, then the links are simply exchanged (this case is referred
            // to as a false merging event)
            if (realParent.equals(clonedParent)) {
                // Remove the real edge between the merging spot and its parent
                Edge edge1 = graph.getEdge(mergingParent, merging);
                removeRealEdge(edge1);

                // Add a real edge between the merging spot and its clone's parent
                addEdge(clonedParent, merging, edge1.getLinkCost(), false);

                // Remove the fake edge between the cloned spot and its parent
                Edge edge2 = graph.getEdge(clonedParent, cloned);
                graph.removeEdge(edge2);

                // Add a fake edge between the cloned spot and the merging spot's parent
                addEdge(mergingParent, cloned, edge2.getLinkCost(), true);

            } // The merging spot is linked to a cloned spot but it is not a false merge 
            else {
                // Remove the real edge between the merging spot and its parent
                Edge edge1 = graph.getEdge(mergingParent, merging);
                removeRealEdge(edge1);

                // Add a real edge between the merging spot and the original spot of its 
                // cloned parent
                addEdge(realParent, merging, edge1.getLinkCost(), false);

                // Add, if applicable, the merging spot's old parent to the child-less 
                // spots' list
                if (getChildren(mergingParent).isEmpty()) {
                    leafs.add(mergingParent);
                }
            }
        } else {
            // If the cloned spot has a merging parent, then, likewise, we link it to the
            // clone of its parent
            if (clonedParent.getState() == Spot.MERGING) {
                // Retrieve the clone of the merging parent
                Spot newParent = mergingToCloned.get(clonedParent);

                // Remove the fake edge between the cloned spot and its parent
                Edge edge2 = graph.getEdge(clonedParent, cloned);
                graph.removeEdge(edge2);

                // Add a fake edge between the cloned spot and the clone of its parent
                addEdge(newParent, cloned, edge2.getLinkCost(), true);

                // Add, if applicable, the cloned spot's old parent to the child-less 
                // spot's list
                if (getChildren(clonedParent).isEmpty()) {
                    leafs.add(clonedParent);
                }
            }
            // NEW THOMAS, test parent and grand parent
            // TODO test ancestor and check age of spot, if < fakelife then delete
            if ((clonedAncestor != null) && (clonedAncestor.isResultOfSplitting())) {
                System.out.println("handling split + merge for " + cloned + " " + clonedParent.getAge());
                if (clonedParent.getAge() < fakeLife) {
                    clonedAncestor.setState(Spot.FAKE);
                    clonedParent.setState(Spot.FAKE);
                    cloned.setState(Spot.FAKE);
                }
//                // delete spots cloned and clonedparent
//
//                //delete edges between cloned and cloned parent   
//                Spot clonePP=getParent(clonedParent);
//                 //
//                //graph.removeEdge(merging, cloned);                
//               
//                
//                graph.removeEdge(clonedParent,clonePP);
//                 graph.removeEdge(clonePP,clonedParent);
//                graph.removeEdge(cloned, clonedParent);
//                 
//                graph.addEdge(cloned,clonePP);
                //clonedToMerging.remove(cloned);
                //leafs.remove(cloned);                //
                //clonedParent.setState(Spot.FAKE);
            }
        }
    }

    /**
     * Analyse the merging spot in the frame (t+1)
     *
     * @param tmp : the merging spot
     */
    private void analyzeMergingSpot(Spot tmp) {
        /* 
         * If two spots have three or four children including a merging spot and its clone,
         * then the associations must be changed in order to address the merging events.
         */
        // Correct, if applicable, the wrong children associations leading to the considered
        // merging spot. This correction might change the state of the merging spot. It will
        // return true if the merging spot remains merging.
        boolean test = correctWrongChildAssociations(tmp);

        // if the merging spot remains merging after the correction of the wrong children
        // associations
        if (test) {
            /*
             * If a fake cell and a real cell merge together, then the merging event is only 
             * the result of the modifications made by the tracking algorithm. It therefore
             * needs to be addressed.
             */
            // Correct the fake merging associations if applicable. This method might also 
            // change the state of the considered spot. It will return true if the spot 
            // remains merging.
            test = correctFakeMergingAssociations(tmp);
        }

        // if the merging spot remains merging after the correction of the fake merging 
        // associations
        if (test) {
            // If the merging spot's age reaches the maximal number of frames allowed for
            // merging spots
            if (tmp.getAge() >= mergingLife) {
                // Sets the merging spot and its merging ancestors to the default state 
                // leading to the suppression of the merging events
                removeMergingSpots(tmp);
            } // the merging spot is still allowed in the graph
            else {
                /*
                 * In this part of the algorithm, we want to check if the merging event is 
                 * the result of an over segmentation problem. We detect this problem when a 
                 * cell divides in two different cells who immediately merge afterwards. The
                 * solution would be to remove the cloned spot and consider the merging spot
                 * as a default one. 
                 * 
                 */
                // Retrieve the parent of the merging spot
                Spot parent = getParent(tmp);
                // Retrieve the clone of the merging spot
                Spot cloned = mergingToCloned.get(tmp);
                // Retrieve the parent of the clone of the merging spot
                Spot clonedParent = getParent(cloned);

                // TEST THOMAS, translate cloned
                double xi = (0.1 * cloned.getPosX() + 0.9 * clonedParent.getPosX());
                double yi = (0.1 * cloned.getPosY() + 0.9 * clonedParent.getPosY());
                double zi = (0.1 * cloned.getPosZ() + 0.9 * clonedParent.getPosZ());

                double xxi = (0.1 * tmp.getPosX() + 0.9 * parent.getPosX());
                double yyi = (0.1 * tmp.getPosY() + 0.9 * parent.getPosY());
                double zzi = (0.1 * tmp.getPosZ() + 0.9 * parent.getPosZ());

                tmp.setPosition(xi, yi, zi);
                cloned.setPosition(xxi, yyi, zzi);

//                System.out.println("Merging " + tmp);
//                System.out.println("Cloned  " + cloned);
//                System.out.println("Parent  " + parent);
//                System.out.println("Cparent " + clonedParent);
                // if the parents of the merging spot and its clone are the result of
                // splitting
                if (parent.isResultOfSplitting() && clonedParent.isResultOfSplitting()) {
                    // Remove the cloned spot from the graph
                    removeClonedSpot(tmp, parent, cloned, clonedParent);

                    // The merging spot's parent is not a really daughter cell and its age 
                    // is set accordingly
                    parent.setAge(getParent(parent).getAge() + 1);

                    // Set the age of the merging spot once its parent's age had been updated
                    tmp.setAge(parent.getAge() + 1);

                    // TODO add a method to flag clonedParent as a falsePositive
                    // may be the link with Etienne algorithm
                    // TODO may be create a single spot from the two daughter cells to truly 
                    // address the over segmentation problem
                }
            }
        }
    }

    /**
     * Removes the cloned spot from the graph leading to the suppression of the
     * merging event
     *
     * @param merging       : the merging spot
     * @param mergingParent : the parent of the merging spot
     * @param cloned        : the cloned spot to be removed
     * @param clonedParent  : the parent of the cloned spot
     * @return the cost of the edge linking the cloned spot to its parent
     */
    private double removeClonedSpot(Spot merging, Spot mergingParent, Spot cloned,
                                    Spot clonedParent) {

        // Retrieve the cost of the edge linking the cloned spot to its parent
        double cost = graph.getEdge(clonedParent, cloned).getLinkCost();

        // Remove the fake edge between the cloned spot and its parent
        graph.removeEdge(cloned, clonedParent);

        // if applicable, remove the cloned spot from the child-less or orphan spots' lists
        if (leafs.contains(cloned)) {
            leafs.remove(cloned);
        }

        // if removing the edge between the cloned spot and its parent lead the parent to 
        // become child-less then it is added to the child-less spots' list
        if (getChildren(clonedParent).isEmpty()) {
            leafs.add(clonedParent);
        }

        // Remove the cloned spot from the graph
        graph.removeVertex(cloned);

        // Remove the cloned spot from the mapping cloned to merging
        clonedToMerging.remove(cloned);

        // Sets the merging spot to default state
        merging.setState(Spot.DEFAULT);

        // Remove the merging spot from the mapping merging to cloned
        mergingToCloned.remove(merging);

        return cost;

    }

    /**
     * Sets the merging spot and its merging ancestors to the default state
     * leading to the suppression of the related merging events
     *
     * @param tmp : the merging spot resulting from the merging event to be
     *            addressed
     */
    private void removeMergingSpots(Spot tmp) {
        // Retrieve the parent of the merging spot
        Spot parent = getParent(tmp);
        // Retrieve the clone of the merging spot
        Spot cloned = mergingToCloned.get(tmp);
        // Retrieve the parent of the cloned spot
        Spot clonedParent = getParent(cloned);

        // Checks if the merging spot is the result of a fake merging event (a merging spot 
        // and its clone merge together leading to another merging spot and another cloned
        // spot)
        if (isFakeMergingSpot(tmp, parent, cloned, clonedParent)) {
            // Remove the cloned spot form the graph
            removeClonedSpot(tmp, parent, cloned, clonedParent);
            // Sets the merging spot's parent and its merging ancestors to the default state
            removeMergingSpots(parent);
        } // If it is a real merging event
        else {
            // Remove the cloned spot from the graph
            removeClonedSpot(tmp, parent, cloned, clonedParent);

            // Updates the merging spot's children's age
            setChildrenAge(tmp);
        }
    }

    /**
     * Correct wrong children associations when two different spots are linked
     * to three or four children including a merging spot and its cloned
     *
     * @param tmp : the merging spot among the children which have been may-be
     *            wrongly associated to their parent
     * @return true if the merging spot remains merging after the execution of
     * this method
     */
    private boolean correctWrongChildAssociations(Spot tmp) {
        // TODO schemas pour expliquer les cas
        // Retrieve the clone of the merging spot
        Spot cloned = mergingToCloned.get(tmp);

        // Retrieve the parent of the merging spot
        Spot parent = getParent(tmp);

        // Retrieve the parent of the cloned spot
        Spot clonedParent = getParent(cloned);

        // Retrieve the children of the merging spot's parent (so the merging spot and its
        // sisters)
        ArrayList<Spot> mergingChildren = getChildren(parent);

        if (mergingChildren.size() == 2) {
            removeClonedSpot(tmp, parent, cloned, clonedParent);
            double cost = removeRealEdge(parent, tmp);
            addEdge(clonedParent, tmp, cost, false);
        }

        return (tmp.getState() == Spot.MERGING);

    }

    private void correctMergingRealAssociations(Spot tmp, Spot parent) {
        Spot cloned = mergingToCloned.get(parent);

        ArrayList<Spot> mergingChildren = getChildren(parent);
        ArrayList<Spot> clonedChildren = getChildren(cloned);

        ArrayList<Edge> edges = new ArrayList<Edge>();

        for (Spot mergingChildren1 : mergingChildren) {
            edges.add(graph.getEdge(parent, mergingChildren1));
        }

        for (Spot clonedChildren1 : clonedChildren) {
            edges.add(graph.getEdge(cloned, clonedChildren1));
        }

        ArrayList<Spot> children = new ArrayList<Spot>();
        for (Edge edge : edges) {
            children.add(graph.getEdgeTarget(edge));
        }

        if (edges.size() > 1) {
            ArrayList<Spot> realParents = new ArrayList<Spot>();

            // remplir real parents
            realParents.add(getParent(getFirstRealMerge(parent, cloned)));
            realParents.add(getParent(mergingToCloned.get(getFirstRealMerge(parent, cloned))));

            double[][] costs = new CostMatrix1(realParents, children, new ArrayList<Penalty>(), new Distance(Double.MAX_VALUE, null, null), Double.MAX_VALUE,
                    Double.MAX_VALUE, Double.MAX_VALUE).getCost();

            int[] vector = new HungarianAlgorithm(costs).execute();

            if (children.get(vector[0]).getState() == Spot.DEFAULT) {

                if (graph.getEdgeSource(edges.get(vector[0])).equals(cloned)) {
                    double cost = removeRealEdge(edges.get(vector[0]));
                    addEdge(parent, children.get(vector[0]), cost, false);
                }
            }

            if (children.get(vector[1]).getState() == Spot.DEFAULT) {
                if (graph.getEdgeSource(edges.get(vector[1])).equals(parent)) {
                    double cost = removeRealEdge(edges.get(vector[1]));
                    addEdge(cloned, children.get(vector[1]), cost, false);
                }
            }

            ArrayList<Spot> newChildren = new ArrayList<Spot>();
            ArrayList<Edge> newEdges = new ArrayList<Edge>();

            for (int i = 0; i < children.size(); i++) {
                if (i != vector[0] && i != vector[1]) {
                    newChildren.add(children.get(i));
                    newEdges.add(edges.get(i));
                }
            }

            costs = new CostMatrix1(realParents, newChildren, new ArrayList<Penalty>(),
                    new Distance(Double.MAX_VALUE, null, null), Double.MAX_VALUE,
                    5, Double.MAX_VALUE).getCost();
            vector = new HungarianAlgorithm(costs).execute();

            if (vector[0] < newChildren.size() && newChildren.get(vector[0]).getState() == Spot.DEFAULT) {

                if (graph.getEdgeSource(newEdges.get(vector[0])).equals(cloned)) {
                    double cost = removeRealEdge(newEdges.get(vector[0]));
                    addEdge(parent, newChildren.get(vector[0]), cost, false);
                }
            }

            if (vector[1] < newChildren.size() && newChildren.get(vector[1]).getState() == Spot.DEFAULT) {

                if (graph.getEdgeSource(newEdges.get(vector[1])).equals(parent)) {
                    double cost = removeRealEdge(newEdges.get(vector[1]));
                    addEdge(cloned, newChildren.get(vector[1]), cost, false);
                }
            }
        }

        mergingChildren = getChildren(parent);
        clonedChildren = getChildren(cloned);

        if (mergingChildren.size() < 2) {
            for (Spot mergingChildren1 : mergingChildren) {
                mergingChildren1.setResultOfSplitting(false);
            }
        }

        if (clonedChildren.size() < 2) {
            for (Spot clonedChildren1 : clonedChildren) {
                clonedChildren1.setResultOfSplitting(false);
            }
        }
    }

    private void endMerge(Spot tmp, Spot parent) {
        // tmp default / parent merging
        correctMergingRealAssociations(tmp, parent);

        Spot cloned = mergingToCloned.get(parent);

        Spot firstRealMerge = getFirstRealMerge(parent, cloned);

        Spot firstRealClonedParent = getParent(mergingToCloned.get(firstRealMerge));

        ArrayList<Spot> clonedChildren = getChildren(cloned);

        for (Spot clonedChildren1 : clonedChildren) {
            double cost = removeRealEdge(cloned, clonedChildren1);
            addEdge(firstRealClonedParent, clonedChildren1, cost, false);
            // TODO predict and  add the missing spots' positions
            // To link with Etienne's algorithm
        }

        removeMergingSpots(parent);
    }

    private void analyzeNormalSpot(Spot tmp) {
        if (!roots.contains(tmp)) {
            Spot parent = getParent(tmp);
            if (parent.getState() == Spot.MERGING) {
                endMerge(tmp, parent);
            } else if (parent.getState() == Spot.FAKE) {
                makeFakeReal(parent);
            }
        }
    }

    private void makeFakeReal(Spot tmp) {
        if (roots.contains(tmp) && tmp.getState() == Spot.FAKE) {
            tmp.setState(Spot.DEFAULT);
            addedSpots.add(tmp);
        } else if (tmp.getState() == Spot.FAKE) {
            tmp.setState(Spot.DEFAULT);
            makeFakeReal(getParent(tmp));
        }

        // TODO link with Etienne algorithm (addedSpots)
    }

    private Spot getFirstRealMerge(Spot merging, Spot cloned) {
        Spot firstRealMerge = merging;
        Spot parent = getParent(merging);
        Spot clonedParent = getParent(cloned);
        if (isFakeMergingSpot(merging, parent, cloned, clonedParent)) {
            firstRealMerge = getFirstRealMerge(parent, clonedParent);
        }

        return firstRealMerge;
    }

    private boolean correctFakeMergingAssociations(Spot tmp) {
        Spot cloned = mergingToCloned.get(tmp);
        Spot parent = getParent(tmp);
        Spot clonedParent = getParent(cloned);
        if (parent.getState() == Spot.FAKE) {
            double cost = removeClonedSpot(tmp, parent, cloned, clonedParent);
            removeRealEdge(parent, tmp);
            leafs.add(parent);
            addEdge(clonedParent, tmp, cost, false);
            if (parent.getAge() < fakeLife) {
                addAFakeCell(parent);
            } else {
                removeFakeCells(parent);
            }
        } else if (clonedParent.getState() == Spot.FAKE) {
            removeClonedSpot(tmp, parent, cloned, clonedParent);
            leafs.add(clonedParent);
            if (clonedParent.getAge() < fakeLife) {
                addAFakeCell(clonedParent);
            } else {
                removeFakeCells(clonedParent);
            }
        }
        return (tmp.getState() == Spot.MERGING);
    }

    private void setChildrenAge(Spot spot) {
        ArrayList<Spot> children = getChildren(spot);
        for (Spot child : children) {
            if (children.size() == 1) {
                child.setAge(spot.getAge() + 1);
            } else {
                child.setAge(1);
            }
            setChildrenAge(child);
        }
    }

    public void end() {
        // Retrieve the new leafs of the tree
        ArrayList<Spot> newLeafs = leafsInLastFrame();
        // Loop through the new leafs of the tree
        for (Spot tmp : newLeafs) {
            if (tmp.getState() == Spot.FAKE) {
                removeFakeCells(tmp);
            } else if (tmp.getState() == Spot.MERGING) {
                removeMergingSpots(tmp);
            }
        }
    }
}
