package amal.penalties;

import amal.tracking.Spot;

/**
 * The Volume class generates a matrix storing the penalties related to the
 * feature "Volume" when the link between two spots is considered.
 *
 * @author Amal Tiss
 */
public class PenaltyColoc extends Penalty {

    /**
     * Constructor
     *
     * @param w: the weight assigned to this penalty
     */
    public PenaltyColoc(double w) {
        super(w);
    }

    /* 
     * Computes the volume penalty when considering the link between spot1 and spot2. Stores
     * the value at the position [i,j] in the penalty matrix.
     */
    public void setPenalty(Spot spot1, Spot spot2, int i, int j) {

        // test coloc
        double vol1 = spot1.volume();
        double vol2 = spot2.volume();
        double coloc = spot1.getObject3D().getColoc(spot2.getObject3D());

        double penaltyTmp = (vol1 + vol2 - coloc) / (vol1 + vol2);
        this.penalty[i][j] = penaltyTmp * penaltyTmp;
        if (penaltyTmp < 1) ;
        //System.out.println("penalty " + i + " " + j + " " + (penaltyTmp*penaltyTmp));

    }

    /* 
     * Computes the area penalty when considering the link between spot1 and spot2. Stores
     * the value at the position [i,j] in the penalty matrix. This method takes into account
     * the nature of linking it is considering (merging or splitting) to compute the penalty.
     */
    @Override
    public void setPenalty(Spot spot1, Spot spot2, Spot spot3, int i, int j, byte operation) {

        // ??

        this.penalty[i][j] = 0;

    }


}
