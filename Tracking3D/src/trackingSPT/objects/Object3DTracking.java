package trackingSPT.objects;
import trackingSPT.mcib3DAdapters.Objects3DPopulationAdapter;

public class Object3DTracking extends AssociationObjectAction {
	
	private Objects3DPopulationAdapter objectT;
	private Objects3DPopulationAdapter objectTPlus1;

	public Object3DTracking(Objects3DPopulationAdapter objectT, Objects3DPopulationAdapter objectTPlus1) {
		this.objectT = objectT;
		this.objectTPlus1 =  objectTPlus1;
	}
	
	@Override
	public Objects3DPopulationAdapter getObjectT() {
		return objectT;
	}
	
	@Override
	public Objects3DPopulationAdapter getObjectTPlus1() {
		return objectTPlus1;
	}
	
}
