package amal.penalties;

import amal.tracking.Spot;

/**
 * The Flatness class generates a matrix storing the penalties related to the feature 
 * "Flatness" when the link between two spots is considered.
 * 
 * @author Amal Tiss
 *
 */
public class PenaltyFlatness extends Penalty{
	
	/**
	 * Constructor
	 * @param w: the weight assigned to this penalty
	 */
	public PenaltyFlatness(double w){
		
		super(w);
	
	}

	/* 
	 * Computes the flatness penalty when considering the link between spot1 and spot2. 
	 * Stores the value at the position [i,j] in the penalty matrix.
	 */
	public void setPenalty(Spot spot1, Spot spot2, int i, int j){
		
		// Retrieve the flatness of each spot
		double flat1 = spot1.flatness();
		double flat2 = spot2.flatness();
		
		// Computes the penalty as explained in 
		// http://fiji.sc/TrackMate_Algorithms#Calculating_linking_costs
		this.penalty[i][j] = 3 * (Math.max(flat1, flat2)- Math.min(flat1, flat2)) /
				(flat1 + flat2);
		
	}
	
	
	/* 
	 * Computes the flatness penalty when considering the link between spot1 and spot2. 
	 * Stores the value at the position [i,j] in the penalty matrix. This method can take into
	 * account the nature of the link considered (gap closing, splitting, merging).
	 */
	public void setPenalty(Spot spot1, Spot spot2, Spot spot3, int i, int j, byte operation){
		
		setPenalty(spot1,spot2,i,j);
		
	}
	
}
