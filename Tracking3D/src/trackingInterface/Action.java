package trackingInterface;

/**
 * 
 * Generic definition of a task executor to strategy object 
 *
 */
public interface Action {

	void setObject(ObjectAction object);
	ObjectAction execute();
	
}
