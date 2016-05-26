package trackingPlugin;
import java.util.ArrayList;
import java.util.List;

public abstract class TrackingStrategy {
	
	private List<TrackingAction> actions;
	private ObjectAction inObject;
	
	public TrackingStrategy(ObjectAction inObject) {
		this.actions = new ArrayList<TrackingAction>();
		this.inObject = inObject;
	}
	
	public abstract void build();
	
	public void run() {
		ObjectAction result = inObject;
		for (TrackingAction trackingAction : actions) {
			result = trackingAction.execute(result);
		}
	}
	
	public void addAction(TrackingAction action) {
		actions.add(action);
	}

}
