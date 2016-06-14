package amal.tracking;


import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;

/**
 * The class Distance computes the distances between all pairs of objects in two consecutive
 * frames and stores them in a matrix. It also enables the user to change the blocking value
 * either in CostMatrix1 or CostMatrix2 and the maximal distance allowed for a link to be
 * considered possible. The maximal distance is updated every time a new spot in the frame
 * (t) is considered in CostMatrix1 and  CostMatrix2.
 *
 * @author Amal Tiss
 */
public class Distance {

    Objects3DPopulation frame1;
    Objects3DPopulation frame2;

    private boolean closest[][];

    // The maximal distance allowed for a link to be considered
    private double distanceMax = Double.MAX_VALUE;

    // The value given to the distance of an impossible link
    private double blockingValue = Double.MAX_VALUE;

    // The matrix where the distances will be stored
    private double[][] distances;

    /**
     * Constructor
     *
     * @param distance: the first value given to the maximal distance allowed
     */
    public Distance(double distance, Objects3DPopulation frame1, Objects3DPopulation frame2) {
        setDistanceMax(distance);
        this.frame1 = frame1;
        this.frame2 = frame2;

        //
        closest = new boolean[frame1.getNbObjects()][frame2.getNbObjects()];
        for (int i = 0; i < frame1.getNbObjects(); i++)
            for (int j = 0; j < frame2.getNbObjects(); j++)
                closest[i][j] = false;
        frame2.updateNamesAndValues();

        for (int i = 0; i < frame1.getNbObjects(); i++) {
            Object3D object3D = frame1.getObject(i);
            Object3D closest1 = frame2.kClosestBorder(object3D, 1);
            Object3D closest2 = frame2.kClosestBorder(object3D, 2);
            if (closest1 != null)
                closest[i][frame2.getIndexFromValue(closest1.getValue())] = true;
            if (closest2 != null)
                closest[i][frame2.getIndexFromValue(closest2.getValue())] = true;
        }
    }

    /**
     * Constructor
     *
     * @param distance: the first value given to the maximal distance allowed
     * @param block:    the first value given to the blocking value
     */
    public Distance(double distance, double block) {

        setDistanceMax(distance);
        setBlockingValue(block);

    }


    /**
     * Instantiates the distances matrix with the suitable size
     *
     * @param nb1: the number of rows in the distances matrix
     * @param nb2: the number of columns in the distances matrix
     */
    public void initDistance(int nb1, int nb2) {
        distances = new double[nb1][nb2];
    }


    /**
     * Computes the distance between two spots and stores it in the distances matrix
     *
     * @param spot1: the spot in the frame (t)
     * @param spot2: the spot in the frame (t+1)
     * @param i:     the position of the row where the computed distance is stored in the matrix
     * @param j:     the position of the column where the computed distance is stored in the
     *               matrix
     */
    public void setDistance(Spot spot1, Spot spot2, int i, int j) {
        double distance = 1;
        int coloc = spot1.getObject3D().getColoc(spot2.getObject3D());
        if (coloc > 0) {
            distance = 0;
        } else {
            if ((frame1 != null) && (frame2 != null)) {
                if (closest[i][j]) distance = 0;
            }
        }

        this.distances[i][j] = distance;



       /* this.distances[i][j] = Math.sqrt(
                (spot1.getPosX() - spot2.getPosX()) * (spot1.getPosX() - spot2.getPosX()) +
                        (spot1.getPosY() - spot2.getPosY()) * (spot1.getPosY() - spot2.getPosY()) +
                        (spot1.getPosZ() - spot2.getPosZ()) * (spot1.getPosZ() - spot2.getPosZ()));
                        */

		/*
     if (spot1.getID() == 964 ) {
		 System.out.println("964 --> "+spot2.getID() + " : " + distances[i][j]);
		// System.out.println("i : "+ i);
		 // System.out.println("j : "+ j);
		 System.out.println("Distance maximale autorisee : " + distanceMax);
	
		 // System.out.println("Age : " + spot1.age());
			// System.out.println(" Search Radius 1 : " + spot1.searchRadius1());
		// System.out.println("Search Radius 2 : " + spot1.searchRadius2());
		// System.out.println("Distance to closest neighbour : " +spot1.distanceToClosestObjectInFrame());
		System.out.println();
	}
	 
	 
	
	// */


        // If the distance between the two spots exceeds the maximal distance allowed, it is
        // set to the blocking value
        if (this.distances[i][j] > distanceMax) {
            //distances[i][j] = blockingValue;
        }
    }

    /**
     * Stores the entered value at the required position in the distances matrix
     *
     * @param i:   the position of the row where the value is stored in the matrix
     * @param j:   the position of the column where the value is stored in the matrix
     * @param val: the value to be stored in the distances matrix
     */
    public void setDistance(int i, int j, double val) {
        distances[i][j] = val;
    }

    /**
     * @param i: the position of the row where the wanted value is stored in the matrix
     * @param j: the position of the column where the wanted value is stored in the matrix
     * @return the distance stored at the entered position
     */
    public double getDistance(int i, int j) {
        return distances[i][j];
    }

    /**
     * Changes the maximal distance allowed to the entered value
     *
     * @param dist: the new maximal distance allowed
     */
    public void setDistanceMax(double dist) {
        distanceMax = dist;
    }


    /**
     * Changes the blocking value to the entered value
     *
     * @param dist: the new blocking value
     */
    public void setBlockingValue(double block) {
        blockingValue = block;
    }

    /**
     * @return the distances matrix
     */
    public double[][] getDistances() {
        return distances;
    }


}
