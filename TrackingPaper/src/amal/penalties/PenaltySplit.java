package amal.penalties;

import amal.tracking.Spot;

/**
 * This class generates a matrix storing the penalties computed based on the a
 * priori knowledge about the cells' behaviour when splitting.
 *
 * @author Amal Tiss
 *
 */
public class PenaltySplit extends Penalty {

    /**
     * Constructor
     *
     * @param w: the weight assigned to this penalty
     */
    public PenaltySplit(double w) {
        super(w);
    }

    /* 
     * Sets the penalty when considering the link between spot1 and spot2 to zero. This 
     * penalty is only meaningful when considering splitting links.
     */
    @Override
    public void setPenalty(Spot spot1, Spot spot2, int i, int j) {
        this.penalty[i][j] = 0;
    }

    /* 
     * Computes the penalty when considering the link between spot1 and spot2. This method 
     * takes into account the nature of the link considered. If it is a splitting, the penalty
     * is computed.
     */
    @Override
    public void setPenalty(Spot spot1, Spot spot2, Spot spot3, int i, int j, byte operation) {
        // The value by default (zero)
        setPenalty(spot1, spot2, i, j);

        // If the link concerns a splitting cell 
        if (operation == 1) {
            // We consider the link between spot1 (t) and spot2 (t+1) knowing that spot1(t) 
            // is already linked to spot3 (t+1) --> spot1 is splitting into spot2 and spot3
            // if spot1 is the direct daughter of a splitting cell
            if (spot1.isResultOfSplitting()) {
                // The link is impossible because two consecutive splits are not possible in
                // our dataset
                // TODO modify the field resultOfSplitting in the class Spot to consider a
                // period during which the splitting is impossible
                penalty[i][j] = Double.MAX_VALUE;
            } // spot1 is not the direct result of a splitting cell
            else {
                // We know that the two daughter cells drift apart from their parent and go 
                // to opposite directions. Therefore, the midpoint between the two daughter 
                // cells should be close to the splitting cell position if the link is truly 
                // a splitting. Since the two frames are separated in time (by timeInterval 
                // in minutes), we allow the midpoint to be a little further than the old 
                // position of the splitting cell. To do so, we compare the distance between 
                // the midpoint and the splitting cell to the splitting cell's mean 
                // displacement.
                // the distance between the midpoint between the daughter cells (spot2 and 
                // spot 3) and the splitting cell (spot1) along the three axes
                double dx = (spot2.getPosX() + spot3.getPosX()) / 2 - spot1.getPosX();
                double dy = (spot2.getPosY() + spot3.getPosY()) / 2 - spot1.getPosY();
                double dz = (spot2.getPosZ() + spot3.getPosZ()) / 2 - spot1.getPosZ();

                // the distance between the midpoint between the daughter cells and the 
                // splitting cell
                double d = Math.sqrt(dx * dx + dy * dy + dz * dz);

                // the difference between the distance previously computed and the splitting
                // cell's mean displacement
                double p = Math.abs(d - spot1.meanDisplacement());

                // the midpoint is very close to the predicted position for the splitting 
                // cell --> this link is very likely
                if (p < 1) {
                    // A negative penalty minimises the cost of the considered link
                    penalty[i][j] = -10;
                }
            }
        }
    }
}
