package amal.penalties;

import amal.tracking.Spot;

/**
 * The Penalty class generates a matrix storing the penalties related to each
 * feature considered when computing the cost of the link between two spots.
 *
 * @author Amal Tiss
 *
 */
public abstract class Penalty {

    public static final byte MERGING = 0;
    public static final byte SPLITTING = 1;

    // The matrix wehre the computed penalties will be stored
    protected double[][] penalty;

    // The weight assigned to this penalty
    protected double weight = 0;

    /**
     * Constructor
     *
     * @param w: the weight assigned to this penalty
     */
    public Penalty(double w) {
        this.weight = w;
    }

    /**
     * Instantiate the penalty matrix
     *
     * @param nb1: the number of rows
     * @param nb2: the number of columns
     */
    public void initPenalty(int nb1, int nb2) {
        this.penalty = new double[nb1][nb2];
    }

    /**
     * Stores the penalty value at the position [i,j] in the penalty matrix.
     *
     * @param i : the row where the penalty will be stored
     * @param j : the column where the penalty will be stored
     * @param val : the value given to the penalty
     */
    public void setPenalty(int i, int j, double val) {
        penalty[i][j] = val;
    }

    /**
     * Computes the wanted penalty when considering the link between spot1 and
     * spot2. Stores the value at the position [i,j] in the penalty matrix.
     *
     * @param spot1 : the spot from the frame (t)
     * @param spot2 : the spot from the frame (t+1)
     * @param i : the row where the penalty will be stored
     * @param j : the column where the penalty will be stored
     */
    public void setPenalty(Spot spot1, Spot spot2, int i, int j) {

        // The penalty will be computed depending on the feature considered
    }

    /**
     * Computes the wanted penalty when considering the link between spot1 and
     * spot2 knowing that a link is already made with spot3. The nature of the
     * link considered (splitting or merging) is taken into account. Stores the
     * value at the position [i,j] in the penalty matrix.
     *
     * @param spot1 : the spot from the frame (t)
     * @param spot2 : the spot from the frame (t+1)
     * @param spot3 : the spot involved in an older link. If the link considered
     * is a splitting one, then spot1 is already linked to spot3. If the link
     * considered is a merging one, then spot3 is already linked to spot2.
     * @param i : the row where the penalty will be stored
     * @param j : the column where the penalty will be stored
     * @param operation
     */
    public void setPenalty(Spot spot1, Spot spot2, Spot spot3, int i, int j, byte operation) {
        // The penalty will be computed depending on the feature considered
    }

    /**
     * Retrieves the value of the penalty stored at the required position
     *
     * @param i : the row where the penalty is stored
     * @param j : the column where the penalty is stored
     * @return the value stored at the position [i,j]
     */
    public double getAPenalty(int i, int j) {

        // if the value to be retrieved is not a number, this method returns zero
        if (Double.isNaN(penalty[i][j])) {
            penalty[i][j] = 0;
        }

        return penalty[i][j];
    }

    /**
     * @return the weight assigned to this penalty
     */
    public double getWeight() {

        return weight;

    }

    /**
     * Sets the penalty's weight to the entered value
     *
     * @param w : the weight assigned to this penalty
     */
    public void setWeight(double w) {

        weight = w;

    }

}
