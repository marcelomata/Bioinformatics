package trackingSPT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import trackingSPT.objects.TemporalObject;

public class MotionField {
	
	private Map<Integer, List<TemporalObject>> mapObjects;
	
	public MotionField() {
		this.mapObjects = new HashMap<Integer, List<TemporalObject>>();
	}
	
	public Map<Integer, List<TemporalObject>> getMapObjects() {
		return mapObjects;
	}
	
	public int getMapSize() {
		return mapObjects.size();
	}
	
	public void addNewObject(TemporalObject object) {
		
	}
	
	

}
