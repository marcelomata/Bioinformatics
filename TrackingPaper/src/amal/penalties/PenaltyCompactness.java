package amal.penalties;

import amal.tracking.Spot;


/**
 * The Compactness class generates a matrix storing the penalties related to the feature 
 * "Compactness" when the link between two spots is considered.
 * 
 * @author Amal Tiss
 *
 */
public class PenaltyCompactness extends Penalty{
	
	
	/**
	 * Constructor
	 * @param w: the weight assigned to this penalty
	 */
	public PenaltyCompactness(double w){		
		super(w);		
	}

	/* 
	 * Computes the compactness penalty when considering the link between spot1 and spot2. 
	 * Stores the value at the position [i,j] in the penalty matrix.
	 */
	public void setPenalty(Spot spot1, Spot spot2, int i, int j){
		
		// Retrieve the compactness of each spot
		double comp1 = spot1.compactness();
		double comp2 = spot2.compactness();
		
		// Computes the penalty as explained in 
		// http://fiji.sc/TrackMate_Algorithms#Calculating_linking_costs
		this.penalty[i][j] = 3 * (Math.max(comp1, comp2)- Math.min(comp1, comp2)) /
				(comp1 + comp2);
	}
	
	
	/* 
	 * Computes the compactness penalty when considering the link between spot1 and spot2. 
	 * Stores the value at the position [i,j] in the penalty matrix. This method can take into
	 * account the nature of the link considered (gap closing, splitting, merging).
	 */
	public void setPenalty(Spot spot1, Spot spot2, Spot spot3, int i, int j, byte operation){
		
		setPenalty(spot1,spot2,i,j);
		
		
	}
}
