package amal.penalties;

import amal.tracking.Spot;

/**
 * The Volume class generates a matrix storing the penalties related to the
 * feature "Volume" when the link between two spots is considered.
 *
 * @author Amal Tiss
 */
public class PenaltyVolume extends Penalty {

    /**
     * Constructor
     *
     * @param w: the weight assigned to this penalty
     */
    public PenaltyVolume(double w) {
        super(w);
    }

    /* 
     * Computes the volume penalty when considering the link between spot1 and spot2. Stores
     * the value at the position [i,j] in the penalty matrix.
     */
    public void setPenalty(Spot spot1, Spot spot2, int i, int j) {



        // Retrieve the area of each spot
        double vol1 = spot1.volume();
        double vol2 = spot2.volume();

        // Computes the penalty as explained in 
        // http://fiji.sc/TrackMate_Algorithms#Calculating_linking_costs
        this.penalty[i][j] = 3 * (Math.max(vol1, vol2) - Math.min(vol1, vol2)) / (vol1 + vol2);

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

            // Compare the volume of the the cells before and after merging
            double vol1 = spot1.volume() + spot3.volume();
            double vol2 = spot2.volume();

            this.penalty[i][j] = 3 * (Math.max(vol1, vol2) - Math.min(vol1, vol2)) / (vol1 + vol2);

        } // Splitting: spot1 splits into spot2 and spot3
        else if (operation == 1) {

            // Compare the volume of the cells before and after splitting
            double vol1 = spot1.volume();
            double vol2 = spot2.volume() + spot3.volume();

            this.penalty[i][j] = 3 * (Math.max(vol1, vol2) - Math.min(vol1, vol2)) / (vol1 + vol2);

        }

    }

}
