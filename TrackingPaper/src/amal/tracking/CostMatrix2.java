package amal.tracking;

import amal.penalties.Penalty;

import java.util.ArrayList;

/**
 * The class CostMatrix2 generates a matrix storing the costs for links made at
 * the second step of the algorithm. These links are more difficult to make than
 * those made in the first step. The associations considered are as follows :
 * <p>
 * - Gap Closing : match the objects of the frame (t) that were not linked in
 * the first step to the unlinked objects of the frame (t+1). These links were
 * impossible before but can be made possible by considering a larger maximal
 * distance allowed via a gap closing coefficient.
 * <p>
 * - Merging : match the unlinked objects of the frame (t) to the already-linked
 * objects of the frame (t+1). I also chose to consider the possibility of
 * changing the maximal distance allowed via a merging coefficient.
 * <p>
 * - Splitting : match the already-linked objects of the frame (t) to the
 * unlinked objects of the frame (t+1). I also considered a splitting
 * coefficient allowing the modification of the maximal distance.
 * <p>
 * - No linking : match the objects to themselves as an alternative to linking.
 * <p>
 * The structure of the cost matrix is as follows:
 * ╔═══════════════╦══════════════╦═════════════╦════════════════╗ ║
 * Unlinked(t+1) ║ Linked(t) ║ Linked(t+1) ║ Unlinked(t) ║
 * ╔═══════════════╬═══════════════╬══════════════╬═════════════╬════════════════╣
 * ║ Unlinked(t) ║ Gap Closing ║ X ║ Merging ║ No Linking ║
 * ╠═══════════════╬═══════════════╬══════════════╬═════════════╬════════════════╣
 * ║ Linked(t) ║ Splitting ║ No Linking ║ X ║ X ║
 * ╠═══════════════╬═══════════════╬══════════════╬═════════════╬════════════════╣
 * ║ Linked(t+1) ║ X ║ X ║ No Linking ║ t(Merging) ║
 * ╠═══════════════╬═══════════════╬══════════════╬═════════════╬════════════════╣
 * ║ Unlinked(t+1) ║ No Linking ║ t(Splitting) ║ X ║ t(Gap Closing) ║
 * ╚═══════════════╩═══════════════╩══════════════╩═════════════╩════════════════╝
 * <p>
 * - The Gap Closing, Merging and Splitting blocks contain the linking costs
 * computed as in the first step (the centre-to-centre distance weighted with
 * penalties). - No linking blocks are filled with a blocking value except for
 * the diagonal terms that are fixed to an alternative cost. - x : a block where
 * all the links are impossible but its presence is necessary to ensure a square
 * matrix. - t(Gap Closing), t(Merging) and t(Splitting) blocks are respectively
 * the transpose of the Gap Closing, Merging and Splitting with minor
 * modifications: the non-blocking values are replaced by the smallest value in
 * the Gap Closing, Merging and Splitting blocks.
 *
 * @author Amal Tiss
 */
public class CostMatrix2 {

    // The unlinked spots of the frame (t)
    private ArrayList<Spot> unlinked1;

    // The unlinked spots of the frame (t+1)
    private ArrayList<Spot> unlinked2;

    // The already-linked spots of the frame (t)
    private ArrayList<Spot> linked1;

    // The already-linked spots of the frame (t+1)
    private ArrayList<Spot> linked2;

    // The spots of the frame (t+1) to which the already-linked spots of the frame (t) are
    // linked
    private ArrayList<Spot> linked1Children;

    // The spots of the frame (t) to which the already-linked spots of the frame (t+1) are
    // linked
    private ArrayList<Spot> linked2Parents;

    // The number of the unlinked spots of the frame (t)
    private int nb1;

    // The number of the unlinked spots of the frame (t+1)
    private int nb2;

    // The number of the already-linked spots of the frame (t)
    private int nb3;

    // The number of the already-linked spots of the frame (t+1)
    private int nb4;

    // The penalties considered for computing the costs
    private ArrayList<Penalty> penalties;

    // Contains the distance matrix and defines the blocking value and the maximal distance
    // allowed for a link to be made. The maximal distance is specific to every spot in the
    // frame (t) and is based on its search radius
    private Distance distance;

    // The default blocking value
    private double blockingValue = Double.MAX_VALUE;

    // The default maximal distance coefficient for gap closing associations. If it is set
    // to a value greater than one, it may enable the previously-doomed links where the 
    // distance between the two cells exceeded the maximal distance allowed
    private double coefGapClose = 1;

    // The default maximal distance coefficient for merging associations
    private double coefMerge = 1;
    // The default maximal distance coefficient for splitting associations
    private double coefSplit = 1;

    // These coefficients permit the user to adapt the maximal distance depending on the
    // nature of the associations to be made.
    // The upper limit for the spots' search radii: if a spot's search radius exceeds the
    // this value, the maximal distance allowed is set to the upper limit
    private double upperLimit;

    // The lower limit for the spots' search radii: if a spot's search radius is smaller
    // than this value, the maximal distance allowed is set to the lower limit
    private double lowerLimit;


    // The matrix containing the costs
    private double[][] cost;

    /**
     * Constructor
     *
     * @param linked1:            the linked spots of the frame (t)
     * @param linked2:            the linked spots of the frame (t+1)
     * @param unlinked1:          the unlinked spots of the frame (t)
     * @param unlinked2:          the unlinked spots of the frame (t+1)
     * @param linked2Parents:     the spots linked to the spots of @param linked2
     * @param linked1Children:the spots linked to the spots of @param linked1
     * @param penalties:          the chosen penalties to compute the costs
     * @param distance:           defines the blocking value and the maximal distance
     *                            allowed for a link to be made
     * @param coef:               the maximal distance coefficient for Gap Closing association
     *                            allowing the previously unlinked objects to consider a higher maximal
     *                            distance
     * @param coefMerge:          the maximal distance coefficient for Merging
     *                            associations
     * @param coefSplit:          the maximal distance coefficient for Splitting
     *                            associations
     * @param upperLimit:         the upper limit for the spots' search radii
     * @param lowerLimit:         the lower limit for the spots' search radii
     * @param blockingValue       : the entered blockingValue
     */
    public CostMatrix2(ArrayList<Spot> linked1, ArrayList<Spot> linked2, ArrayList<Spot> unlinked1, ArrayList<Spot> unlinked2, ArrayList<Spot> linked2Parents, ArrayList<Spot> linked1Children, ArrayList<Penalty> penalties, Distance distance, double coefGapClose, double coefMerge, double coefSplit, double upperLimit, double lowerLimit, double blockingValue) {

        this.linked1 = linked1;
        this.linked2 = linked2;
        this.unlinked1 = unlinked1;
        this.unlinked2 = unlinked2;

        this.linked1Children = linked1Children;
        this.linked2Parents = linked2Parents;

        this.nb1 = unlinked1.size();
        this.nb2 = unlinked2.size();
        this.nb3 = linked1.size();
        this.nb4 = linked2.size();

        // The cost matrix is a instantiated as a square matrix
        this.cost = new double[nb1 + nb2 + nb3 + nb4][nb1 + nb2 + nb3 + nb4];

        this.penalties = penalties;
        this.distance = distance;

        // It is possible to consider another blocking value
        this.blockingValue = blockingValue;
        distance.setBlockingValue(blockingValue);

        // For every penalty, a matrix is instantiated where the penalty to link objects
        // and will later be saved
        for (int i = 0; i < this.penalties.size(); i++) {
            this.penalties.get(i).initPenalty(nb1 + nb3 + nb4, nb2 + nb3 + nb4);
        }

        // A distance matrix is also instantiated and will contain the distances
        // between the objects
        this.distance.initDistance(nb1 + nb3 + nb4, nb2 + nb3 + nb4);

        this.coefGapClose = coefGapClose;
        this.coefMerge = coefMerge;
        this.coefSplit = coefSplit;

        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }

    /**
     * Fills the Gap Closing, Merging and Splitting blocks with the suitable
     * costs
     */
    private void computeCosts() {
        // Loop through unlinked1 elements
        for (int i = 0; i < nb1; i++) {
            // For every unlinked spot in the frame (t)
            Spot tmp1 = unlinked1.get(i);
            // Compute the search radius as the maximum between the motion-based search
            // radius (1) and the local-density-based search radius (2)
            double searchRadius = Math.max(tmp1.searchRadius1(), tmp1.searchRadius2());
            // Check that the search radius is greater than the lower limit
            searchRadius = Math.max(searchRadius, lowerLimit);
            // Loop through unlinked2 elements (Gap Closing block)
            for (int j = 0; j < nb2; j++) {
                // For every unlinked spot in the frame (t+1)
                Spot tmp2 = unlinked2.get(j);
                // Compute the searchRadius for the operation Gap Closing
                double searchRadiusGapClosing = this.coefGapClose * searchRadius;
                // Check that the search radius is smaller than the upper limit
                searchRadiusGapClosing = Math.min(upperLimit, searchRadiusGapClosing);
                // Set the maximal distance allowed to the obtained search radius
                distance.setDistanceMax(searchRadiusGapClosing);
                // Loop through the chosen penalties
                for (Penalty penaltie : penalties) {
                    // Compute the penalty assigned to the link between the spots tmp1 and
                    // tmp2 and store it at the position (i,j) in the penalty matrix
                    penaltie.setPenalty(tmp1, tmp2, i, j);
                }

                // Compute the distance between the spots tmp1 and tmp2 and store the value
                // at the position (i,j) of the distance matrix
                distance.setDistance(tmp1, tmp2, i, j);
            }

            // Loop through linked2 elements (Merging block)
            for (int j = 0; j < nb4; j++) {
                // For every linked spot in the frame (t+1)
                Spot tmp2 = linked2.get(j);
                // Retrieve the spot of the frame (t) to which tmp2 is linked
                Spot tmp3 = linked2Parents.get(j);
                // Compute the search radius for merging associations
                double searchRadiusMerging = searchRadius * coefMerge;
                // Check that the search radius is smaller than the upper limit
                searchRadiusMerging = Math.min(upperLimit, searchRadiusMerging);
                // Set the maximal distance allowed to the obtained search radius
                distance.setDistanceMax(searchRadiusMerging);
                // Loop through the chosen penalties
                for (Penalty penaltie : penalties) {
                    // Compute the penalty assigned to the link between the spots tmp1 and
                    // tmp2 and store it at the position (i,j+nb2+nb3) in the penalty matrix
                    // For the merging operation, the penalties are computed differently
                    // to take into account the link already made for the merging spot
                    // thus the use of tmp3 and the merging flag
                    penaltie.setPenalty(tmp1, tmp2, tmp3, i, j + nb2 + nb3, Penalty.MERGING);
                }

                // Compute the distance between the spots tmp1 and tmp2 and store the value
                // at the position (i,j+nb2+nb3) of the distance matrix
                distance.setDistance(tmp1, tmp2, i, j + nb2 + nb3);
            }
        }

        // Loop through linked1 elements (Splitting block)
        for (int i = 0; i < nb3; i++) {
            // For every linked spot in the frame (t)
            Spot tmp1 = linked1.get(i);
            // Retrieve the spot of the frame (t+1) to which tmp1 is linked
            Spot tmp3 = linked1Children.get(i);
            // Compute the search radius as the maximum between the motion-based search
            // radius (1) and the local-density-based search radius (2)
            double searchRadiusSplitting = Math.max(tmp1.searchRadius1(), tmp1.searchRadius2());
            // Check that the search radius is greater than the lower limit
            searchRadiusSplitting = Math.max(searchRadiusSplitting, lowerLimit);
            // Adapt the search radius to splitting operation
            searchRadiusSplitting = coefSplit * searchRadiusSplitting;
            // Check that the search radius is smaller than the upperLimit
            searchRadiusSplitting = Math.min(searchRadiusSplitting, upperLimit);
            // Set the maximal distance allowed to the obtained search radius
            distance.setDistanceMax(searchRadiusSplitting);

            // Loop through unlinked2 elements
            for (int j = 0; j < nb2; j++) {
                // For every unlinked spot in the frame (t+1)
                Spot tmp2 = unlinked2.get(j);
                // Loop through the chosen penalties
                for (Penalty penaltie : penalties) {
                    // Compute the penalty assigned to the link between the spots tmp1 and
                    // tmp2 and store it at the position (i+nb1,j) in the penalty matrix
                    // For the splitting operation, the penalties are computed differently
                    // to take into account the link already made for the splitting spot 
                    // thus the use of tmp3 and the splitting flag
                    penaltie.setPenalty(tmp1, tmp2, tmp3, i + nb1, j, Penalty.SPLITTING);
                }

                // Compute the distance between the spots tmp1 and tmp2 and store the value
                // at the position (i+nb1,j) of the distance matrix
                distance.setDistance(tmp1, tmp2, i + nb1, j);
            }
        }

        // Loop through the rows of the Gap Closing and the Splitting blocks
        for (int i = 0; i < nb1 + nb3; i++) {
            // Loop through the columns of the Gap Closing and the Merging blocks
            for (int j = 0; j < nb2 + nb3 + nb4; j++) {
                if (j < nb2 || j >= nb2 + nb3) {
                    // Initialise the sum of weighted penalties to link the spot i in the
                    // frame (t) to the spot j in the frame (t+1)
                    double penalty = 0;
                    // Loop through the chosen penalties
                    for (Penalty penaltie : penalties) {
                        // For every penalty, update the sum of weighted penalties
                        penalty += penaltie.getAPenalty(i, j) * penaltie.getWeight();
                    }
                    penalty = Math.max(0, penalty);

                    // Compute the cost to link the spot i in the frame (t) to the spot j in
                    // the frame (t+1) based on the distance and the weighted penalties
                    if (distance.getDistance(i, j) == 0) cost[i][j] = penalty;
                    else cost[i][j] = Double.MAX_VALUE;
                    //cost[i][j] = Math.pow(distance.getDistance(i, j) * (1 + penalty), 2);
                    // If the cost exceeds the blocking value, it's set to the blocking value
                    cost[i][j] = Math.min(cost[i][j], blockingValue);
                }
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
    private void computeAllCosts() {
        // Fill in the Gap Closing, the Merging and the Splitting blocks
        computeCosts();

        // Compute the alternative cost as 1.05 * max (Gap Closing, Merging and Splitting
        // blocks). Since the other blocks of the cost matrix are not filled yet, it is 
        // equivalent to compute the maximal non-blocking cost value in cost
        double alternativeCost = 1.05 * max(cost);

        // Compute the smallest value is the Gap Closing, Merging and Splitting blocks.
        // Since the other blocks of the cost matrix are not filled yet (set to zero by 
        // default), I chose to consider the smallest strictly positive value
        double min = min(cost);

        // Loop through unlinked1 elements
        for (int i = 0; i < nb1; i++) {
            // Loop through linked1 elements ( X block)
            for (int j = 0; j < nb3; j++) {
                // Set the  cost to the blocking value to make the link impossible
                cost[i][j + nb2] = blockingValue;
            }

            // Loop through unlinked1 elements (No Linking block)
            for (int j = nb2 + nb3 + nb4; j < nb1 + nb2 + nb3 + nb4; j++) {
                // Set the  cost to the blocking value to make the link impossible
                cost[i][j] = blockingValue;
            }
            // Set the diagonal terms of the No Linking block to the alternative cost
            cost[i][i + nb2 + nb3 + nb4] = alternativeCost;
        }

        // Loop through linked1 elements
        for (int i = nb1; i < nb1 + nb3; i++) {
            // Loop through linked1 elements (No Linking block)
            for (int j = nb2; j < nb2 + nb3; j++) {
                // Set the  cost to the blocking value to make the link impossible
                cost[i][j] = blockingValue;
            }
            // Set the diagonal terms of the No Linking block to the alternative cost
            cost[i][i - nb1 + nb2] = alternativeCost;

            // Loop through linked2 elements (X block)
            for (int j = nb2 + nb3; j < nb2 + nb3 + nb4; j++) {
                // Set the  cost to the blocking value to make the link impossible
                cost[i][j] = blockingValue;
            }

            // Loop through unlinked1 elements (X block)
            for (int j = nb2 + nb3 + nb4; j < nb2 + nb3 + nb4 + nb1; j++) {
                // Set the  cost to the blocking value to make the link impossible
                cost[i][j] = blockingValue;
            }
        }

        // Loop through linked2 elements
        for (int i = nb1 + nb3; i < nb1 + nb3 + nb4; i++) {
            // Loop through unlinked2 elements (X block)
            for (int j = 0; j < nb2; j++) {
                // Set the  cost to the blocking value to make the link impossible
                cost[i][j] = blockingValue;
            }

            // Loop through linked1 elements (X block) 
            for (int j = nb2; j < nb2 + nb3; j++) {
                // Set the  cost to the blocking value to make the link impossible
                cost[i][j] = blockingValue;
            }

            // Loop through linked2 elements (No Linking block)
            for (int j = nb2 + nb3; j < nb2 + nb3 + nb4; j++) {
                // Set the  cost to the blocking value to make the link impossible
                cost[i][j] = blockingValue;
            }
            // Set the diagonal terms of the No Linking block to the alternative cost
            cost[i][i - nb1 + nb2] = alternativeCost;

            // Loop through unlinked1 elements (t(Merging) block)
            for (int j = nb2 + nb3 + nb4; j < nb2 + nb3 + nb4 + nb1; j++) {
                // The transpose of the Merging block
                cost[i][j] = cost[j - nb2 - nb3 - nb4][i - nb1 + nb2];

                // Replace the non-blocking value with the smallest value in Gap Closing,
                // Merging and Splitting blocks
                if (cost[i][j] < blockingValue) {
                    cost[i][j] = min;
                }
            }
        }

        // Loop through unlinked2 elements 
        for (int i = nb1 + nb3 + nb4; i < nb1 + nb2 + nb3 + nb4; i++) {
            // Loop through unlinked2 elements (No Linling block)
            for (int j = 0; j < nb2; j++) {
                // Set the  cost to the blocking value to make the link impossible
                cost[i][j] = blockingValue;
            }
            // Set the diagonal terms of the No Linking block to the alternative cost
            cost[i][i - nb1 - nb3 - nb4] = alternativeCost;

            // Loop through linked1 elements (t(Splitting) block)
            for (int j = nb2; j < nb2 + nb3; j++) {
                // The transpose of the Splitting block
                cost[i][j] = cost[j - nb2 + nb1][i - nb1 - nb3 - nb4];
                // Replace the non-blocking value with the smallest value in Gap Closing,
                // Merging and Splitting blocks
                if (cost[i][j] < blockingValue) {
                    cost[i][j] = min;
                }
            }

            // Loop through linked2 elements (X block)
            for (int j = nb2 + nb3; j < nb2 + nb3 + nb4; j++) {
                // Set the  cost to the blocking value to make the link impossible
                cost[i][j] = blockingValue;
            }

            // Loop through unlinked1 elements (t(Gap Closing))
            for (int j = nb2 + nb3 + nb4; j < nb1 + nb2 + nb3 + nb4; j++) {
                // The transpose of the Gap Closing block
                cost[i][j] = cost[j - nb2 - nb3 - nb4][i - nb1 - nb3 - nb4];
                // Replace the non-blocking value with the smallest value in Gap Closing,
                // Merging and Splitting blocks
                if (cost[i][j] < blockingValue) {
                    cost[i][j] = min;
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
        computeAllCosts();

        return cost;
    }

}
