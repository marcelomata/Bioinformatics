package amal.tracking;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;



/**
 * The class Edge models a link between two spots. It stores their IDs and the cost of their 
 * link. It also stores the displacement of the cell from the source spot position to the 
 * target spot's.
 * This class is also necessary to generate the XML file used in TrackMate.
 *
 * @author Amal Tiss
 *
 */
@XmlRootElement
public class Edge {
	
	@XmlAttribute(name="SPOT_SOURCE_ID")
	private int SPOT_SOURCE_ID;
	
	@XmlAttribute(name="SPOT_TARGET_ID")
	private int SPOT_TARGET_ID;
	
	// The cost computed in the cost matrix to establish the link between the source and
	// target spots
	private double LINK_COST;
	
	// The displacement of the cell between the source spot position and the target spot's
	private double displacement;
	
	// The velocity of the cell during its displacement from the source's position to the 
	// target's
	@XmlAttribute(name="VELOCITY")
	private double velocity;
	
	// A flag to distinguish real edges linking two spots and those added by the algorithm 
	// to check if the associations are correct
	private boolean fake;
	
	/**
	 * Constructor
	 * Sets default values to the required fields. This method is necessary to generate the 
	 * XML file
	 */
	public Edge(){
		
		SPOT_SOURCE_ID=-1;
		SPOT_TARGET_ID=-1;
		LINK_COST = Double.MAX_VALUE;
		
	}
	
	/**
	 * Sets the source spot ID to the entered value
	 * @param i: the ID of the source spot
	 */
	public void setSpotSourceID(int i){
		SPOT_SOURCE_ID=i;
	}
	
	
	/**
	 * Sets the target spot ID to the entered value
	 * @param i: the ID of the target spot
	 */
	public void setSpotTargetID(int i){
		SPOT_TARGET_ID=i;
	}
	
	/**
	 * Sets the link's cost to the entered value
	 * @param c: the cost of the link
	 */
	public void setLinkCost(double c){
		LINK_COST=c;
	}
	
	/**
	 * @return the cost of the link
	 */
	@XmlAttribute(name="LINK_COST")
	public double getLinkCost(){
		return LINK_COST;
	}
	
	
	/**
	 * Sets the displacement to the entered value
	 * @param d: the value of the cell's displacement from the source's position to the
	 * target's
	 */
	public void setDisplacement(double d){
		displacement = d;
	}
	
	/**
	 * @return the value of the cell's displacement from the source's position to the
	 * target's
	 */
	@XmlAttribute(name="DISPLACEMENT")
	public double getDisplacement(){
		return displacement;
	}
	
	/**
	 * Sets the cell's velocity related to this link to the entered value
	 * @param v : the value of the cell's velocity during its displacement from  the 
	 * source'sposition to the target's
	 */
	public void setVelocity(double v){
		// Not used for now but present in the XML file
		// TODO : add a method to compute this feature: velocity=displacement/timeInterval
		velocity = v;
	}
	
	
	/**
	 * Returns true if the edge has been added by the algorithm to check if the associations 
	 * made are correct. The added edge is fake only during the verification step. If the 
	 * tracking is false and the correction made needs an added edge, then the added edge is
	 * not considered fake. However, if the algorithm concludes that the associations are
	 * correct, the edge added to perform the verification are deleted.
	 * @return the value of the fake flag
	 */
	public boolean isFake(){
		return fake;
	}
	
	
	/**
	 * Sets the fake flag to the entered value
	 * @param fictive: the entered value for the fake flag
	 */
	public void fake(boolean fictive){
		this.fake = fictive;
	}

	
}
