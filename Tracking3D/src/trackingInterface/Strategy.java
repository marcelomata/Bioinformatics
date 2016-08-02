package trackingInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * This abstract class defines an engine that controll the execution of actions 
 *
 */
public abstract class Strategy {
	
	private List<Action> actions;
	private int currentAction;
	
	public Strategy() {
		this.actions = new ArrayList<Action>();
		this.currentAction = 0;
	}

	protected abstract void build();
	
	public abstract void run();

	public void addAction(Action action) {
		this.actions.add(action);
	}
	
	protected Action nextAction() {
		Action result = actions.get(currentAction);
		currentAction = (currentAction + 1) % actions.size();
		return result;
	}
	
	public void clear() {
		this.actions.clear();
		build();
	}
	
	public int getNumberOfActions() {
		return this.actions.size();
	}
	
}
