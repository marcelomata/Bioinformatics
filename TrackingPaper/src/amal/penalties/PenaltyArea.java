package amal.penalties;

import amal.tracking.Spot;

/**
 * The Area class generates a matrix storing the penalties related to the
 * feature "Area" when the link between two spots is considered.
 *
 * @author Amal Tiss
 *
 */
public class PenaltyArea extends Penalty {

    /**
     * Constructor
     *
     * @param w: the weight assigned to this penalty
     */
    public PenaltyArea(double w) {
        super(w);
    }


    /* 
     * Computes the area penalty when considering the link between spot1 and spot2. Stores
     * the value at the position [i,j] in the penalty matrix.
     */
    @Override
    public void setPenalty(Spot spot1, Spot spot2, int i, int j) {

        // Retrieve the area of each spot
        double area1 = spot1.area();
        double area2 = spot2.area();

        // Computes the penalty as explained in 
        // http://fiji.sc/TrackMate_Algorithms#Calculating_linking_costs
        this.penalty[i][j] = 3 * (Math.max(area1, area2) - Math.min(area1, area2))
                / (area1 + area2);
    }


    /* 
     * Computes the area penalty when considering the link between spot1 and spot2. Stores
     * the value at the position [i,j] in the penalty matrix. This method takes into account
     * the nature of linking it is considering (merging or splitting) to compute the penalty.
     */
    @Override
    public void setPenalty(Spot spot1, Spot spot2, Spot spot3, int i, int j, byte operation) {

        // Merging: spot1 and spot3 merge into spot2
        if (operation == 0) {

            // Compare the area of the the cells before and after merging
            double area1 = spot1.area() + spot3.area();
            double area2 = spot2.area();

            this.penalty[i][j] = 3 * (Math.max(area1, area2) - Math.min(area1, area2)) / (area1 + area2);

        } // Splitting: spot1 splits into spot2 and spot3
        else if (operation == 1) {
            // Compare the area of the cells before and after splitting
            double area1 = spot1.area();
            double area2 = spot2.area() + spot3.area();

            this.penalty[i][j] = 3 * (Math.max(area1, area2) - Math.min(area1, area2)) / (area1 + area2);
        }
    }

}
