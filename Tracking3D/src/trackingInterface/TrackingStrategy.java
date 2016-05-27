package trackingInterface;
import java.util.ArrayList;
import java.util.List;

public abstract class TrackingStrategy {
	
	private List<TrackingAction> actions;
	private int currentAction;
	protected Object4D inObject;
	
	public TrackingStrategy(Object4D inObject) {
		this.actions = new ArrayList<TrackingAction>();
		this.inObject = inObject;
		this.currentAction = 0;
		build();
	}
	
	protected abstract void build();
	
	public abstract void run();
	
	protected void addAction(TrackingAction action) {
		actions.add(action);
	}
	
	protected TrackingAction nextAction() {
		TrackingAction result = actions.get(currentAction);
		currentAction = (currentAction + 1) % actions.size();
		return result;
	}

}
