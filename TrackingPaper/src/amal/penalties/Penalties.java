package amal.penalties;


import java.util.ArrayList;

/**
 * The Penalties class groups the penalties considered to compute the linking costs.
 * 
 * @author Amal Tiss
 * 
 */
public class Penalties {
	
	// the list of the penalties considered
	private ArrayList<Penalty> penalties;
	
	
	/**
	 * Constructor : instantiates the list of the penalties to be considered
	 */
	public Penalties(){		
		penalties = new ArrayList<Penalty>();		
	}
	
	/**
	 * Adds the entered penalty to the list of penalties considered.
	 * @param penalty : the penalty considered to compute the linking costs
	 */
	public void add(Penalty penalty){		
		penalties.add(penalty);	
	}
	
	/**
	 * @return the list of penalties considered to compute the linking costs
	 */
	public ArrayList<Penalty> get(){		
		return penalties;	
	}

}
