package trackingSPT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mcib3d.geom.Object3D;
import trackingSPT.objects.ObjectTree;

public class MotionField {
	
	private Map<Integer, List<ObjectTree>> mapObjects;
	private Map<Integer, List<ObjectTree>> mapFinishedObjects;
	private int numberMaxPerId;
	private static Integer id = 1;
	
	public MotionField() {
		this.mapObjects = new HashMap<Integer, List<ObjectTree>>();
		this.mapFinishedObjects = new HashMap<Integer, List<ObjectTree>>();
		this.numberMaxPerId = 0;
	}
	
	public Map<Integer, List<ObjectTree>> getMapObjects() {
		return mapObjects;
	}
	
	public int getSize() {
		Set<Integer> keys = mapObjects.keySet();
		return mapObjects.get(keys.iterator().next()).size();
	}
	
	public void addNewObject(ObjectTree object) {
		List<ObjectTree> list = new ArrayList<ObjectTree>();
		object.setId(id);
		if(numberMaxPerId == 0) {
			numberMaxPerId++;
		} else {
			for (int i = 0; i < numberMaxPerId-1; i++) {
				list.add(new ObjectTree(null));
			}
		}
		list.add(object);
		mapObjects.put(id, list);
		id++;
	}
	
	public void addNewObjectId(Integer idObject, ObjectTree object) {
		object.setId(idObject);
		mapObjects.get(idObject).add(object);
		if(mapObjects.get(idObject).size() > numberMaxPerId) {
			numberMaxPerId = mapObjects.get(idObject).size();
		}
	}

	public List<ObjectTree> getListLastObjects() {
		List<ObjectTree> result = new ArrayList<ObjectTree>();
		List<ObjectTree> temp = null;
		int size = 0;
		Set<Integer> keySet = mapObjects.keySet();
		ObjectTree last = null;
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
	
	public ObjectTree removeLastObject(Integer idObject) {
		List<ObjectTree> temp = mapObjects.get(idObject);
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

	private void fillResultMap(Map<Integer, List<Object3D>> result, Map<Integer, List<ObjectTree>> mapTemporalObjects, Set<Integer> objectKeys) {
		List<ObjectTree> resultsByTrack;
		List<Object3D> trackObject3DList;
		for (Integer integer : objectKeys) {
			resultsByTrack = mapTemporalObjects.get(integer);
			trackObject3DList = new ArrayList<Object3D>();
			for (ObjectTree objectTracked : resultsByTrack) {
				trackObject3DList.add(objectTracked.getObject());
			}
			result.put(integer, trackObject3DList);
		}
	}

	public void addVoidObjectFinishedTrack() {
		Set<Integer> objectKeys = mapFinishedObjects.keySet();
		for (Integer integer : objectKeys) {
			mapFinishedObjects.get(integer).add(new ObjectTree(null));
		}
		
		
	}

}
