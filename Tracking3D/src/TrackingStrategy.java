import java.util.List;

public abstract class TrackingStrategy {
	
	private List<TrackingAction> actions;
	
	
	
	public abstract void build();
	
	public void run() {
		ObjectAction result = null;
		for (TrackingAction trackingAction : actions) {
			result = trackingAction.execute(result);
		}
	}
	
	public void addAction(TrackingAction action) {
		actions.add(action);
	}

}
