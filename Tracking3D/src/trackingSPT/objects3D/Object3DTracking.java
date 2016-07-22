package trackingSPT.objects3D;
import trackingInterface.Frame;

public class Object3DTracking implements TemporalPopulation3D {
	
	private Frame objectT;
	private Frame objectTPlus1;

	public Object3DTracking(Frame objectT, Frame objectTPlus1) {
		this.objectT = objectT;
		this.objectTPlus1 =  objectTPlus1;
	}
	
	@Override
	public Frame getObjectT() {
		return objectT;
	}
	
	@Override
	public Frame getObjectTPlus1() {
		return objectTPlus1;
	}
	
}
