package trackingSPT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mcib3d.geom.Object3D;
import trackingSPT.objects.TemporalObject;

public class MotionField {
	
	private Map<Integer, List<TemporalObject>> mapObjects;
	private Map<Integer, List<TemporalObject>> mapFinishedObjects;
	private int numberMaxPerId;
	private static Integer id = 1;
	
	public MotionField() {
		this.mapObjects = new HashMap<Integer, List<TemporalObject>>();
		this.mapFinishedObjects = new HashMap<Integer, List<TemporalObject>>();
		this.numberMaxPerId = 0;
	}
	
	public Map<Integer, List<TemporalObject>> getMapObjects() {
		return mapObjects;
	}
	
	public int getSize() {
		Set<Integer> keys = mapObjects.keySet();
		return mapObjects.get(keys.iterator().next()).size();
	}
	
	public void addNewObject(TemporalObject object) {
		List<TemporalObject> list = new ArrayList<TemporalObject>();
		object.setId(id);
		if(numberMaxPerId == 0) {
			numberMaxPerId++;
		} else {
			for (int i = 0; i < numberMaxPerId-1; i++) {
				list.add(new TemporalObject(null));
			}
		}
		list.add(object);
		mapObjects.put(id, list);
		id++;
	}
	
	public void addNewObjectId(Integer idObject, TemporalObject object) {
		object.setId(idObject);
		mapObjects.get(idObject).add(object);
		if(mapObjects.get(idObject).size() > numberMaxPerId) {
			numberMaxPerId = mapObjects.get(idObject).size();
		}
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
	
	public Map<Integer, List<Object3D>> getFinalResult() {
		Map<Integer, List<Object3D>> result = new HashMap<Integer, List<Object3D>>();
		Set<Integer> objectKeys = mapObjects.keySet();
		fillResultMap(result, mapObjects, objectKeys);
		objectKeys = mapFinishedObjects.keySet();
		fillResultMap(result, mapFinishedObjects, objectKeys);
		return result;
	}

	private void fillResultMap(Map<Integer, List<Object3D>> result, Map<Integer, List<TemporalObject>> mapTemporalObjects, Set<Integer> objectKeys) {
		List<TemporalObject> resultsByTrack;
		List<Object3D> trackObject3DList;
		for (Integer integer : objectKeys) {
			resultsByTrack = mapTemporalObjects.get(integer);
			trackObject3DList = new ArrayList<Object3D>();
			for (TemporalObject objectTracked : resultsByTrack) {
				trackObject3DList.add(objectTracked.getObject());
			}
			result.put(integer, trackObject3DList);
		}
	}

	public void addVoidObjectFinishedTrack() {
		Set<Integer> objectKeys = mapFinishedObjects.keySet();
		for (Integer integer : objectKeys) {
			mapFinishedObjects.get(integer).add(new TemporalObject(null));
		}
		
		
	}

}
