package amal.penalties;

import amal.tracking.Spot;


/**
 * The Sphericity class generates a matrix storing the penalties related to the feature 
 * "Sphericity" when the link between two spots is considered.
 * 
 * @author Amal Tiss
 *
 */
public class PenaltySphericity extends Penalty{
	
	/**
	 * Constructor
	 * @param w: the weight assigned to this penalty
	 */
	public PenaltySphericity(double w){
		
		super(w);
		
	}

	
	/* 
	 * Computes the sphericity penalty when considering the link between spot1 and spot2. 
	 * Stores the value at the position [i,j] in the penalty matrix.
	 */
	public void setPenalty(Spot spot1, Spot spot2, int i, int j){

		// Retrieve the sphericity of each spot
		double sph1 = spot1.sphericity();
		double sph2 = spot2.sphericity();
		
		// Computes the penalty as explained in 
		// http://fiji.sc/TrackMate_Algorithms#Calculating_linking_costs		
		this.penalty[i][j] = 3 * (Math.max(sph1, sph2)- Math.min(sph1, sph2)) / (sph1 + sph2);
	
	}

	
	/* 
	 * Computes the sphericity penalty when considering the link between spot1 and spot2. 
	 * Stores the value at the position [i,j] in the penalty matrix. This method can take into
	 * account the nature of the link considered (gap closing, splitting, merging).
	 */
	public void setPenalty(Spot spot1, Spot spot2, Spot spot3, int i, int j, byte operation){
		
		setPenalty(spot1,spot2,i,j);
		
	}
	
	
}