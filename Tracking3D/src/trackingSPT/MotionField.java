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
	private Map<Integer, List<ObjectTree3D>> mapMissedObjects;
	private static Integer id = 1;
	
	public MotionField() {
		this.mapObjects = new HashMap<Integer, List<ObjectTree3D>>();
		this.mapFinishedObjects = new HashMap<Integer, List<ObjectTree3D>>();
		this.mapMissedObjects = new HashMap<Integer, List<ObjectTree3D>>();
	}
	
	public Map<Integer, List<ObjectTree3D>> getMapObjects() {
		return mapObjects;
	}
	
	public int getNumberOfTracks() {
		return mapObjects.keySet().size();
	}
	
	public void addNewObject(ObjectTree3D object) {
		System.out.println("Creating object " + id + " - Frame " + object.getFrame());
		List<ObjectTree3D> list = new ArrayList<ObjectTree3D>();
		object.setId(id);
		list.add(object);
		mapObjects.put(id, list);
		id++;
	}
	
	public void addNewObjectId(Integer idObject, ObjectTree3D object) {
		System.out.println("Adding object to track " + idObject + " - Frame " + object.getFrame() + " - Object Value "+object.getObject().getValue());
		object.setId(idObject);
		List<ObjectTree3D> list = mapObjects.get(idObject);
		list.add(object);
	}
	
	public void reconnectMissedObjectId(Integer idObject) {
		List<ObjectTree3D> list = mapMissedObjects.remove(idObject);
		System.out.println("Reconnecting object to track " + idObject + " - Frame " + list.get(list.size()-1).getFrame());
		mapObjects.put(idObject, list);
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
	
	public List<ObjectTree3D> getListObjectsFrame(int frame) {
		List<ObjectTree3D> result = new ArrayList<ObjectTree3D>();
		List<ObjectTree3D> temp = null;
		int size = 0;
		Set<Integer> keySet = mapObjects.keySet();
		for (Integer integer : keySet) {
			temp = mapObjects.get(integer);
			size = temp.size();
			if(size > 0) {
				for (ObjectTree3D t : temp) {
					if(t.getFrame() == frame && !t.isMissed()) {
						result.add(t);
					}
				}
			}
		}
		return result;
	}
	
	public ObjectTree3D removeLastObject(Integer idObject) {
		List<ObjectTree3D> temp = mapObjects.get(idObject);
		System.out.println("Removing last object track " + idObject + " - Frame " + temp.get(temp.size()-1).getFrame());
		return temp.remove(temp.size()-1);
	}
	
	public ObjectTree3D removeObjectFrame(Integer idObject, int frame) {
		List<ObjectTree3D> temp = mapObjects.get(idObject);
		for (ObjectTree3D objectTree3D : temp) {
			if(objectTree3D.getFrame() == frame) {
				if(temp.remove(objectTree3D)) {
					return objectTree3D;
				}
			}
		}
		return null;
	}
	
	public ObjectTree3D getLastObject(Integer idObject) {
		List<ObjectTree3D> temp = mapObjects.get(idObject);
		return temp.get(temp.size()-1);
	}

	public ObjectTree3D getObjectFrame(Integer idObject, int frame) {
		List<ObjectTree3D> temp = mapObjects.get(idObject);
		for (ObjectTree3D objectTree3D : temp) {
			if(objectTree3D.getFrame() == frame) {
				return objectTree3D;
			}
		}
		return null;
	}
	
	public void finishObject(ObjectTree3D obj) {
		System.out.println("Finishing track " + obj.getId() + " - Frame " + obj.getFrame());
		List<ObjectTree3D> temp = mapObjects.remove(obj.getId());
		mapFinishedObjects.put(obj.getId(), temp);
	}
	
	public Map<Integer, List<ObjectTree3D>> getFinalResultByTrack() {
		Map<Integer, List<ObjectTree3D>> result = new HashMap<Integer, List<ObjectTree3D>>();
		Set<Integer> objectKeys = mapObjects.keySet();
		fillResultMap(result, mapObjects, objectKeys);
		objectKeys = mapFinishedObjects.keySet();
		fillResultMap(result, mapFinishedObjects, objectKeys);
		objectKeys = mapMissedObjects.keySet();
		fillResultMap(result, mapMissedObjects, objectKeys);
		return result;
	}
	
	public List<List<ObjectTree3D>> getFinalResultByFrame() {
		List<List<ObjectTree3D>> result = new ArrayList<List<ObjectTree3D>>();
		Set<Integer> objectKeys = mapObjects.keySet();
		fillResultMap(result, mapObjects, objectKeys);
		objectKeys = mapFinishedObjects.keySet();
		fillResultMap(result, mapFinishedObjects, objectKeys);
		objectKeys = mapMissedObjects.keySet();
		fillResultMap(result, mapMissedObjects, objectKeys);
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
	
	private void fillResultMap(List<List<ObjectTree3D>> result, Map<Integer, List<ObjectTree3D>> mapTemporalObjects, Set<Integer> objectKeys) {
		List<ObjectTree3D> resultsByTrack;
		int frame;
		for (Integer integer : objectKeys) {
			resultsByTrack = mapTemporalObjects.get(integer);
			for (ObjectTree3D objectTracked : resultsByTrack) {
				frame = objectTracked.getFrame();
				if(frame > result.size()-1) {
					for (int i = result.size(); i <= frame; i++) {
						result.add(new ArrayList<ObjectTree3D>());
					}
				}
				result.get(frame).add(objectTracked);
			}
		}
	}

//	public void addVoidObjectFinishedTrack(int frame) {
//		Set<Integer> objectKeys = mapFinishedObjects.keySet();
//		ObjectTree3D nullObject;
//		ObjectTree3D obj;
//		List<ObjectTree3D> list;
//		for (Integer integer : objectKeys) {
//			nullObject = new ObjectTree3D(null, frame);
//			nullObject.setId(integer);
//			list = mapFinishedObjects.get(integer);
//			obj = list.get(list.size()-1);
//			obj.addChild(nullObject);
//			nullObject.setParent(obj);
//			list.add(nullObject);
//		}
//	}

//	public boolean isDifferentNumber() {
//		Set<Integer> keys = mapObjects.keySet();
//		List<ObjectTree3D> list; 
//		int number = 0;
//		boolean first = true;
//		int count = 0;
//		for (Integer key : keys) {
//			if(key==65)count++;
//			list = mapObjects.get(key);
//			if(first) {
//				number = list.size();
//				first = false;
//			} else if(number != list.size()) {
//				System.out.println("number - "+count);
//				return true;
//			}
//		}
//		
//		keys = mapFinishedObjects.keySet();
//		first = true;
//		for (Integer key : keys) {
//			if(key==65)count++;
//			list = mapFinishedObjects.get(key);
//			if(first) {
//				number = list.size();
//			} else if(number != list.size()) {
//				System.out.println("number - "+count);
//				return true;
//			}
//		}
//		System.out.println("number - "+count);
//		
//		return false;
//	}
	
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
			list = mapFinishedObjects.get(key);
			System.out.print(key+"-"+list.size()+", ");
		}
		System.out.println();
	}

	public boolean contains(ObjectTree3D obj1) {
		return mapObjects.containsKey(obj1.getId());
	}

	public void setObjectMissed(ObjectTree3D objMissed) {
		System.out.println("Putting track " + objMissed.getId() + " - Frame " + objMissed.getFrame()+" as missed");
		List<ObjectTree3D> temp = mapObjects.remove(objMissed.getId());
		mapMissedObjects.put(objMissed.getId(), temp);
	}

}
