package trackingSTP.impl;
import trackingInterface.ObjectAction;

public class Object3DTracking implements ObjectAction {
	
	private Objects3DPopulationAdapter objectT;
	private Objects3DPopulationAdapter objectTPlus1;

	public Object3DTracking(Objects3DPopulationAdapter objectT, Objects3DPopulationAdapter objectTPlus1) {
		this.objectT = objectT;
		this.objectTPlus1 =  objectTPlus1;
	}
	
	public Objects3DPopulationAdapter getObjectT() {
		return objectT;
	}
	
	public Objects3DPopulationAdapter getObjectTPlus1() {
		return objectTPlus1;
	}
	
}
