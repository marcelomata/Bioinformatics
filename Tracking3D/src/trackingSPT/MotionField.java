package trackingSPT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import trackingSPT.objects.TemporalObject;

public class MotionField {
	
	private Map<Integer, List<TemporalObject>> mapObjects;
	private Map<Integer, List<TemporalObject>> mapFinishedObjects;
	private static Integer id = 1;
	
	public MotionField() {
		this.mapObjects = new HashMap<Integer, List<TemporalObject>>();
		this.mapFinishedObjects = new HashMap<Integer, List<TemporalObject>>();
	}
	
	public Map<Integer, List<TemporalObject>> getMapObjects() {
		return mapObjects;
	}
	
	public int getMapSize() {
		return mapObjects.size();
	}
	
	public void addNewObject(TemporalObject object) {
		List<TemporalObject> list = new ArrayList<TemporalObject>();
		object.setId(id);
		list.add(object);
		mapObjects.put(id, list);
		id++;
	}
	
	public void addNewObjectId(Integer idObject, TemporalObject object) {
		mapObjects.get(idObject).add(object);
		object.setId(idObject);
	}

	public List<TemporalObject> getListLastObjects() {
		List<TemporalObject> result = new ArrayList<TemporalObject>();
		List<TemporalObject> temp = null;
		int size = 0;
		Set<Integer> keySet = mapObjects.keySet();
		TemporalObject last = null;
		for (Integer integer : keySet) {
			temp = mapObjects.get(integer);
			size = temp.size();
			if(size > 0) {
				last = temp.get(size-1);
				if(!last.isMissed()) {
					result.add(last);
				}
			}
		}
		return result;
	}

	public TemporalObject removeLastObject(Integer idObject) {
		List<TemporalObject> temp = mapObjects.get(idObject);
		return temp.remove(temp.size()-1);
	}

	public void finishObject(Integer idObject) {
		mapFinishedObjects.put(idObject, mapObjects.remove(idObject));
	}

}
