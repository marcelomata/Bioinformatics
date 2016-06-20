package trackingSPT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import trackingSPT.objects3D.ObjectTree3D;

public class MotionField {
	
	private Map<Integer, List<ObjectTree3D>> mapObjects;
	private Map<Integer, List<ObjectTree3D>> mapFinishedObjects;
	private int numberMaxPerId;
	private static Integer id = 1;
	
	public MotionField() {
		this.mapObjects = new HashMap<Integer, List<ObjectTree3D>>();
		this.mapFinishedObjects = new HashMap<Integer, List<ObjectTree3D>>();
		this.numberMaxPerId = 0;
	}
	
	public Map<Integer, List<ObjectTree3D>> getMapObjects() {
		return mapObjects;
	}
	
	public int getSize() {
		Set<Integer> keys = mapObjects.keySet();
		return mapObjects.get(keys.iterator().next()).size();
	}
	
	public void addNewObject(ObjectTree3D object) {
		List<ObjectTree3D> list = new ArrayList<ObjectTree3D>();
		object.setId(id);
		if(numberMaxPerId == 0) {
			numberMaxPerId++;
		} else {
			for (int i = 0; i < numberMaxPerId-1; i++) {
				list.add(new ObjectTree3D(null));
			}
		}
		list.add(object);
		mapObjects.put(id, list);
		id++;
	}
	
	public void addNewObjectId(Integer idObject, ObjectTree3D object) {
		object.setId(idObject);
		mapObjects.get(idObject).add(object);
		if(mapObjects.get(idObject).size() > numberMaxPerId) {
			numberMaxPerId = mapObjects.get(idObject).size();
		}
	}

	public List<ObjectTree3D> getListLastObjects() {
		List<ObjectTree3D> result = new ArrayList<ObjectTree3D>();
		List<ObjectTree3D> temp = null;
		int size = 0;
		Set<Integer> keySet = mapObjects.keySet();
		ObjectTree3D last = null;
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
	
	public ObjectTree3D removeLastObject(Integer idObject) {
		List<ObjectTree3D> temp = mapObjects.get(idObject);
		return temp.remove(temp.size()-1);
	}

	public void finishObject(Integer idObject) {
		mapFinishedObjects.put(idObject, mapObjects.remove(idObject));
	}
	
	public Map<Integer, List<ObjectTree3D>> getFinalResult() {
		Map<Integer, List<ObjectTree3D>> result = new HashMap<Integer, List<ObjectTree3D>>();
		Set<Integer> objectKeys = mapObjects.keySet();
		fillResultMap(result, mapObjects, objectKeys);
		objectKeys = mapFinishedObjects.keySet();
		fillResultMap(result, mapFinishedObjects, objectKeys);
		return result;
	}

	private void fillResultMap(Map<Integer, List<ObjectTree3D>> result, Map<Integer, List<ObjectTree3D>> mapTemporalObjects, Set<Integer> objectKeys) {
		List<ObjectTree3D> resultsByTrack;
		List<ObjectTree3D> trackObject3DList;
		for (Integer integer : objectKeys) {
			resultsByTrack = mapTemporalObjects.get(integer);
			trackObject3DList = new ArrayList<ObjectTree3D>();
			for (ObjectTree3D objectTracked : resultsByTrack) {
				trackObject3DList.add(objectTracked);
			}
			result.put(integer, trackObject3DList);
		}
	}

	public void addVoidObjectFinishedTrack() {
		Set<Integer> objectKeys = mapFinishedObjects.keySet();
		ObjectTree3D nullObject;
		ObjectTree3D obj;
		List<ObjectTree3D> list;
		for (Integer integer : objectKeys) {
			nullObject = new ObjectTree3D(null);
			nullObject.setId(integer);
			list = mapFinishedObjects.get(integer);
			obj = list.get(list.size()-1);
			obj.addChild(nullObject);
			mapFinishedObjects.get(integer).add(nullObject);
		}
	}

	public boolean isDifferentNumber() {
		Set<Integer> keys = mapObjects.keySet();
		List<ObjectTree3D> list; 
		int number = 0;
		boolean first = true;
		int count = 0;
		for (Integer key : keys) {
			if(key==65)count++;
			list = mapObjects.get(key);
			if(first) {
				number = list.size();
				first = false;
			} else if(number != list.size()) {
				System.out.println("number - "+count);
				return true;
			}
		}
		
		keys = mapFinishedObjects.keySet();
		first = true;
		for (Integer key : keys) {
			if(key==65)count++;
			list = mapObjects.get(key);
			if(first) {
				number = list.size();
			} else if(number != list.size()) {
				System.out.println("number - "+count);
				return true;
			}
		}
		System.out.println("number - "+count);
		
		return false;
	}
	
	public void printSize() {
		Set<Integer> keys = mapObjects.keySet();
		List<ObjectTree3D> list; 
		for (Integer key : keys) {
			list = mapObjects.get(key);
			System.out.print(key+"-"+list.size()+", ");
		}
		System.out.println();
		
		keys = mapFinishedObjects.keySet();
		for (Integer key : keys) {
			list = mapObjects.get(key);
			System.out.print(key+"-"+list.size()+", ");
		}
		System.out.println();
	}

}
