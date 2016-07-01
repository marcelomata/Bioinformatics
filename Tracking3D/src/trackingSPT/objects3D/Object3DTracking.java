package trackingSPT.objects3D;
import trackingSPT.mcib3DObjects.Objects3DPopulationSPT;

public class Object3DTracking implements TemporalPopulation3D {
	
	private Objects3DPopulationSPT objectT;
	private Objects3DPopulationSPT objectTPlus1;

	public Object3DTracking(Objects3DPopulationSPT objectT, Objects3DPopulationSPT objectTPlus1) {
		this.objectT = objectT;
		this.objectTPlus1 =  objectTPlus1;
	}
	
	@Override
	public Objects3DPopulationSPT getObjectT() {
		return objectT;
	}
	
	@Override
	public Objects3DPopulationSPT getObjectTPlus1() {
		return objectTPlus1;
	}
	
}
