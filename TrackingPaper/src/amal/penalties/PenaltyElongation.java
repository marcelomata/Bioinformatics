package amal.penalties;

import amal.tracking.Spot;


/**
 * The Elongation class generates a matrix storing the penalties related to the feature 
 * "Elongation" when the link between two spots is considered.
 * 
 * @author Amal Tiss
 *
 */
public class PenaltyElongation extends Penalty{
	
	/**
	 * Constructor
	 * @param w: the weight assigned to this penalty
	 */
	public PenaltyElongation(double w){
		
		super(w);
		
	}

	/* 
	 * Computes the elongation penalty when considering the link between spot1 and spot2. 
	 * Stores the value at the position [i,j] in the penalty matrix.
	 */
	public void setPenalty(Spot spot1, Spot spot2, int i, int j){
		
		// Retrieve the elongation of each spot
		double elo1 = spot1.elongation();
		double elo2 = spot2.elongation();
		
		// Computes the penalty as explained in 
		// http://fiji.sc/TrackMate_Algorithms#Calculating_linking_costs
		this.penalty[i][j] = 3 * (Math.max(elo1, elo2)- Math.min(elo1, elo2)) /
				(elo1 + elo2);
		
	}

	/* 
	 * Computes the elongation penalty when considering the link between spot1 and spot2. 
	 * Stores the value at the position [i,j] in the penalty matrix. This method can take into
	 * account the nature of the link considered (gap closing, splitting, merging).
	 */
	public void setPenalty(Spot spot1, Spot spot2, Spot spot3, int i, int j, byte operation){

		setPenalty(spot1, spot2, i, j);

	}

}
