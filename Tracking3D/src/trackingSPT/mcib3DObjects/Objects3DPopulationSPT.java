package trackingSPT.mcib3DObjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.image3d.ImageInt;
import trackingInterface.Frame;

public class Objects3DPopulationSPT implements Frame {
	
	private Objects3DPopulation object3D;
	private ImageInt zones;
	private Map<Integer, Object3D> mapZonesObject;
	
	public Objects3DPopulationSPT(Objects3DPopulation object3D) {
		this.object3D = object3D;
		this.mapZonesObject = new HashMap<Integer, Object3D>();
	}
	
	public Objects3DPopulation getObject3D() {
		return object3D;
	}

	public void setZones(ImageInt zones) {
		this.zones = zones;
		computeZonesObjects();
	}

	private void computeZonesObjects() {
		List<Object3D> objects = object3D.getObjectsList();
		int zone;
		for (Object3D obj : objects) {
			zone = zones.getPixelInt(obj.getCenterAsPoint());
			mapZonesObject.put(zone, obj);
		}
	}
	
	
	@Override
	public ImageInt getZones() {
		return zones;
	}
	

}
