package amal.tracking;

import amal.penalties.Penalty;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The class CostMatrix1 generates a matrix storing the cost of linking any
 * object in a frame to any object in an another frame (normally the following
 * one). It is implemented as shown in figure 1-(b) of Jaqaman's paper "Robust
 * single particle tracking in live cell time lapse sequences".
 * <p>
 * The cost matrix is composed of four blocks:
 * <p>
 * -the top left block: the number of rows is equal to the number of the objects
 * in the first frame (related to the time point t) whereas the number of
 * columns is equal to the number of the objects in the following frame (related
 * to the time point t+1). This block contains the cost of linking any object in
 * frame (t) to any object in frame (t+1). To compute the linking cost, the
 * centre-to-centre distance is weighted (differently) by geometry-based
 * penalties. If the distance between the two objects exceeds a defined maximal
 * distance, the link is considered impossible. To implement this idea, the cost
 * is set to a very high value referred to as the "blocking value" making the
 * link very unlikely to be made.
 * <p>
 * -the top right block: the number of rows and the number of columns both equal
 * the number of objects in the frame (t). Since it is impossible to link two
 * objects in the same frame, this block is filled with the blocking value
 * except for the diagonal terms which contain the costs to link the objects in
 * the frame (t) to themselves. This idea models the possibility of non-linking:
 * if an object is assigned to itself because it leads to the least overall
 * cost, then the object remains unlinked --> end of tracklet. The value given
 * to the diagonal terms is referred to as alternative value in Jaqaman's paper
 * since it is enables the no-linking alternative. It is slightly greater than
 * the maximal non-blocking value in the top left block so that the no-linking
 * is only considered when it is impossible to find a suitable link.
 * <p>
 * -the bottom left block: the number of rows and the number of columns both
 * equal the number of objects in the frame (t+1). This block is very similar to
 * the top right block. The only difference lies in the signification of
 * diagonal terms. They still reflect the idea of assigning the object of frame
 * (t+1) to itself, but this situation now means that a new tracklet is starting
 * with the considered object.
 * <p>
 * -the bottom right block: the number of rows is equal to the number of the
 * objects in the frame (t+1) whereas the number of columns is equal to the
 * number of the objects in the frame (t). Costs in this block do not have any
 * meaning, but they are mathematically required to solve the linear assignment
 * problem. The block is merely the transpose of the top left block with minor
 * modifications: each non-blocking value in the top left block is replaced with
 * the smallest cost computed.
 * <p>
 * The following sums up the structure of the matrix :
 * ╔════════════════════════════════════╦══════════════════════╗ ║ objects(t+1)
 * ║ objects(t) ║ ╠════════════════════════════════════╬══════════════════════╣
 * ║ 1 2 . . . n(t+1) ║ 1 2 . . . n(t) ║
 * ╔════════╬════════════════════════════════════╬══════════════════════╣ ║ 1 ║
 * c11 c12 . . . c1n(t+1) ║ d x . . . x ║ ║ 2 ║ c21 x . . . c2n(t+1) ║ x d . . .
 * x ║ ║ . ║ . . . . ║ . . . . ║ ║ . ║ . . . . ║ . . . . ║ ║ . ║ . . . . ║ . . .
 * . ║ ║ n(t) ║ c1n(t) x . . . cn(t)n(t+1) ║ x x . . . d ║
 * ╠════════╬════════════════════════════════════╬══════════════════════╣ ║ 1 ║
 * d x . . . x ║ s s . . . s ║ ║ 2 ║ x d . . . x ║ s x . . . x ║ ║ . ║ . . . . ║
 * . . . . ║ ║ . ║ . . . . ║ . . . . ║ ║ . ║ . . . . ║ . . . . ║ ║ n(t+1) ║ x x
 * . . . d ║ s s . . . s ║
 * ╚════════╩════════════════════════════════════╩══════════════════════╝ - d
 * refers to the alternative value - s is the minimal value in the top left
 * block - x is the blocking value - cij is the cost to link the spot i in the
 * frame (t) to the spot j in the frame (t+1)
 *
 * @author Amal Tiss
 */
public class CostMatrix1 {

    // Contains the spots detected in the frame (t)
    private ArrayList<Spot> spots1;
    // Contains the spots detected in the frame (t+1)
    private ArrayList<Spot> spots2;

    // The number of the spots detected in the frame (t)
    private int nb1;
    // The number of the spots detected in the frame (t+1)
    private int nb2;

    // The matrix containing the costs
    private double[][] cost;

    // The default blocking value
    private double blockingValue = Double.MAX_VALUE;

    // The penalties considered for computing the costs
    private ArrayList<Penalty> penalties;

    // Contains the distance matrix and defines the blocking value and the maximal distance
    // allowed for a link to be made. The maximal distance is specific to every spot in the
    // frame (t) and is based on its search radius
    private Distance distance;

    // The upper limit for the spots' search radii: if a spot's search radius exceeds the
    // this value, the maximal distance allowed is set to the upper limit.
    private double upperLimit;
    // The lower limit for the spots' search radii: if a spot's search radius is smaller
    // than this value, the maximal distance allowed is set to the lower limit.
    private double lowerLimit;

    /**
     * Constructor
     *
     * @param spots1:       the spots detected in the frame (t)
     * @param spots2:       the spots detected in the frame (t+1)
     * @param penalties:    the penalties considered to compute the cost
     * @param distance:     defines the blocking value and the maximal distance
     *                      allowed for a link to be made
     * @param upperLimit:   the upper limit for the spots' search radii
     * @param lowerLimit:   the lower limit for the spots' search radii
     * @param blockingValue : the entered value for the blocking value
     */
    public CostMatrix1(ArrayList<Spot> spots1, ArrayList<Spot> spots2, ArrayList<Penalty> penalties, Distance distance, double upperLimit, double lowerLimit, double blockingValue) {

        this.spots1 = spots1;
        this.spots2 = spots2;

        this.nb1 = spots1.size();
        this.nb2 = spots2.size();

        // The cost matrix is necessarily a square matrix
        this.cost = new double[nb1 + nb2][nb1 + nb2];

        this.penalties = penalties;
        this.distance = distance;

        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;

        // It is possible to consider another blocking value
        this.blockingValue = blockingValue;
        distance.setBlockingValue(blockingValue);

        // For every penalty, a matrix [nb1*nb2] is instantiated where the penalty to
        // link the object in the frame (t) to an object in the frame (t+1) will later be
        // saved
        for (Penalty penalty : this.penalties) {
            penalty.initPenalty(nb1, nb2);
        }

        // A distance matrix [nb1*nb2] is also instantiated and will contain the distances
        // between the objects in the frame (t) and those in the frame (t+1)
        this.distance.initDistance(nb1, nb2);
    }

    /**
     * Fills the top left block with the suitable costs
     */
    private void computeLinkingCosts() {
        // Loop through spots1 elements
        for (int i = 0; i < nb1; i++) {
            // For every spot in the frame (t)
            Spot tmp1 = spots1.get(i);
            // Compute the search radius as the maximum between the motion-based search
            // radius (1) and the local-density-based search radius (2)
            double searchRadius = Math.max(tmp1.searchRadius1(), tmp1.searchRadius2());
            // Check that the search radius is smaller than the upper limit
            searchRadius = Math.min(searchRadius, upperLimit);
            // Check that the search radius is greater than the lower limit
            searchRadius = Math.max(searchRadius, lowerLimit);
            // Set the maximal distance allowed to the obtained search radius
            distance.setDistanceMax(searchRadius);
            // Loop through spots2 elements
            for (int j = 0; j < nb2; j++) {
                // For every spot in the frame (t+1)
                Spot tmp2 = spots2.get(j);
                // Loop through the chosen penalties
                for (Penalty penalty : penalties) {
                    // Compute the penalty assigned to the link between the spots tmp1 and
                    // tmp2 and store it at the position (i,j) in the penalty matrix
                    penalty.setPenalty(tmp1, tmp2, i, j);
                }
                // Compute the distance between the spots tmp1 and tmp2 and store the value
                // at the position (i,j) of the distance matrix
                distance.setDistance(tmp1, tmp2, i, j);
            }
        }




        // Loop through the rows of the penalties' matrices (or the distance matrix)
        for (int i = 0; i < nb1; i++) {
            // Loop through the columns of the penalties' matrices (or the distance matrix)
            for (int j = 0; j < nb2; j++) {
                // Initialise the sum of weighted penalties to link the spot i in the frame
                // (t) to the spot j in the frame (t+1)
                double penalty = 0;

                // Loop through the penalties
                for (Penalty penalty1 : penalties) {
                    // For every penalty, update the sum of weighted penalties
                    penalty += penalty1.getAPenalty(i, j) * penalty1.getWeight();
                }

                // Compute the cost to link the spot i in the frame (t) to the spot j in
                // the frame (t+1) based on the distance and the weighted penalties
                // TEST if dist=0 only penalty then
                if (distance.getDistance(i, j) == 0) cost[i][j] = penalty;
                else cost[i][j] = Double.MAX_VALUE;
                //cost[i][j] = Math.pow(distance.getDistance(i, j) * (1 + penalty), 2);
                // If the cost exceeds the blocking value, it is set to the blocking value
                cost[i][j] = Math.min(cost[i][j], blockingValue);
               // System.out.println("cost for " + i + " " + j + "  " + cost[i][j]);
            }
        }
    }

    /**
     * Computes the maximal non-blocking value in a given matrix
     *
     * @param matrix
     * @return the maximal non-blocking value in @param matrix
     */
    private double max(double[][] matrix) {

        double res = Double.NEGATIVE_INFINITY;

        // number of rows in matrix
        int nrows = matrix.length;
        // number of columns in matrix
        int ncols = matrix[0].length;

        // Loop through the rows of matrix
        for (int i = 0; i < nrows; i++) {

            // Loop through the columns of matrix
            for (int j = 0; j < ncols; j++) {

                if ((matrix[i][j] < blockingValue) && (matrix[i][j] > res)) {
                    res = matrix[i][j];
                }
            }
        }

        return res;
    }

    /**
     * Computes the smallest, strictly positive, value in a given matrix
     *
     * @param matrix
     * @return the smallest, strictly positive, value in @param matrix
     */
    private double min(double[][] matrix) {

        double res = Double.POSITIVE_INFINITY;

        // number of rows in matrix
        int nrows = matrix.length;
        // number of columns in matrix
        int ncols = matrix[0].length;

        // Loop through the rows of matrix
        for (int i = 0; i < nrows; i++) {

            // Loop through the columns of matrix
            for (int j = 0; j < ncols; j++) {

                if (matrix[i][j] > 0 && matrix[i][j] < res) {
                    res = matrix[i][j];
                }
            }
        }

        return res;
    }

    /**
     * Fills the cost matrix with the suitable values
     */
    private void computeCost() {

        // Fill the top left block 
        computeLinkingCosts();

        // Compute the alternative cost as 1.05 * max (top left block). Since the other
        // blocks of the cost matrix are not filled yet, max(top left block) = max (cost)
        // max returns the maximal non blocking value
        double alternativeCost = 1.05 * max(cost);

        // Compute the smallest value is the top left block. Since the other blocks of the
        // cost matrix are not filled yet (set to zero by default), I chose to consider the
        // smallest strictly positive value
        double min = min(cost);

        // Loop through the rows of the cost matrix 
        for (int i = 0; i < nb1 + nb2; i++) {
            if (i < nb1) {
                // Fill the top right block				
                Arrays.fill(cost[i], nb2, nb1 + nb2, blockingValue);
                cost[i][nb2 + i] = alternativeCost;
            } else {
                // Fill the bottom left block
                Arrays.fill(cost[i], 0, nb2, blockingValue);
                cost[i][i - nb1] = alternativeCost;
            }
        }

        // Loop through the rows of the top left block
        for (int i = 0; i < nb1; i++) {
            // Loop through the columns of the top left block
            for (int j = 0; j < nb2; j++) {
                // Fill the bottom right block as the transpose of the top left block
                cost[nb1 + j][nb2 + i] = cost[i][j];
                // Set all the non-blocking values in the bottom right block to the
                // smallest, strictly positive, value in the top left block
                if (cost[i][j] < blockingValue) {
                    cost[nb1 + j][nb2 + i] = min;
                }
            }
        }
    }

    /**
     * Computes and returns the cost matrix for matching the objects in two
     * consecutive frames
     *
     * @return the cost matrix
     */
    public double[][] getCost() {
        // Computes the cost matrix
        computeCost();

        return cost;
    }
}
