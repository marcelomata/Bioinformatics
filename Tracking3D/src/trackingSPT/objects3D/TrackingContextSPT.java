package trackingSPT.objects3D;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mcib3d.geom.Objects3DPopulation;
import trackingSPT.events.Event;
import trackingSPT.events.EventMapItem;
import trackingSPT.events.enums.EventType;
import trackingSPT.events.eventfinder.EventSeekerObjInterface;
import trackingSPT.events.eventhandler.HandlerObject;
import trackingSPT.segmentation.SegmentationObject;

public class TrackingContextSPT implements SegmentationObject, EventSeekerObjInterface, HandlerObject {
	
	private TemporalPopulation3D temporalPopulation;
	private List<MissedObject> misses;
	private File rawDataDir;
	private File segmentedDataDir;

	/////////////////
	//ObjectActionSPT
	private TrackingResultObjectAction result;
	
	/////////////////////////////////////////////
	//AssociatedObjectList -> SplittingMergingObj
	private Map<ObjectTree3D, List<ObjectTree3D>> associationsMap;
	private List<ObjectTree3D> leftTargetObjects;
	private List<ObjectTree3D> leftSourceObjects;

	//////////////////////////////////////
	private Map<EventType, List<Event>> eventListMap;
	
	public TrackingContextSPT(TrackingResult3DSPT result, File segmentedDataDir, File rawDataDir) {
		this.result = result;
		this.misses = new ArrayList<MissedObject>();
		this.segmentedDataDir = segmentedDataDir;
		this.rawDataDir = rawDataDir;
		
		clear();
	}

	///////////////////////
	//AssocietadObjectsList
	@Override
	public Map<ObjectTree3D, List<ObjectTree3D>> getAssociationsMap() {
		return this.associationsMap;
	}
	
	@Override
	public List<ObjectTree3D> getLeftTargetObjects() {
		return this.leftTargetObjects;
	}

	@Override
	public List<ObjectTree3D> getLeftSourceObjects() {
		return this.leftSourceObjects;
	}

	@Override
	public List<ObjectTree3D> getAssociationsMapSources() {
		Set<ObjectTree3D> sourcesSet = associationsMap.keySet();
		List<ObjectTree3D> sourcesList = new ArrayList<ObjectTree3D>();
		for (ObjectTree3D temporalObject : sourcesSet) {
			sourcesList.add(temporalObject);
		}
		return sourcesList;
	}

	/////////////////
	//ObjectActionSPT
	@Override
	public TrackingResultObjectAction getResult() {
		return this.result;
	}

	//////////////////////////////////////
	//EventMap -> EventHandlerObjectAction
	public void addEventItem(EventMapItem item) {
		List<Event> list = this.eventListMap.get(item.getEventsType());
		if(list == null) {
			list = new ArrayList<Event>();
			this.eventListMap.put(item.getEventsType(), list);
		}
		list.addAll(item.getEventList());
	}
	
	public List<Event> getEventList(EventType type) {
		return eventListMap.get(type);
	}

	public void addEventType(EventType type) {
		if(!this.eventListMap.containsKey(type)) {
			this.eventListMap.put(type, new ArrayList<Event>());
		}
	}
	
	//////////////////////////
	//AssociationSeeker Object
	@Override
	public Objects3DPopulation getObjectNextFrame() {
		return temporalPopulation.getObjectTPlus1().getObject3D();
	}
	
	@Override
	public void addAssociation(ObjectTree3D source, ObjectTree3D target) {
		if(this.associationsMap.containsKey(source)) {
			this.associationsMap.get(source).add(target);
		} else {
			List<ObjectTree3D> newList = new ArrayList<ObjectTree3D>();
			newList.add(target);
			this.associationsMap.put(source, newList);
		}
	}
	
	public List<ObjectTree3D> getListLastObjects() {
		return this.result.getListLastObjects();
	}
	
	public int getCurrentFrame() {
		return result.getCurrentFrame();
	}

	@Override
	public void addAllLeftTargetObjects(List<ObjectTree3D> leftTargetObjects) {
		this.leftTargetObjects.addAll(leftTargetObjects);
	}

	@Override
	public void addAllLeftSourceObjects(List<ObjectTree3D> leftSourceObjects) {
		this.leftSourceObjects.addAll(leftSourceObjects);
	}

	@Override
	public void addMissed(ObjectTree3D objMissed) {
		System.out.println("Adding object track "+objMissed.getId()+" - Frame "+objMissed.getFrame()+" to missed list");
		this.misses.add(new MissedObject(objMissed, result.getCurrentFrame()));
		this.result.setObjectMissed(objMissed);
	}

	@Override
	public void addNewObjectId(Integer id, ObjectTree3D treeObj) {
		result.addNewObjectId(id, treeObj);
	}

	@Override
	public List<MissedObject> getMisses() {
		return this.misses;
	}
	
	public void setTemporalPopulation(TemporalPopulation3D temporalPopulation) {
		this.temporalPopulation = temporalPopulation;
	}
	
	public void clear() {
		//////////////////////////////////////
		//EventMap -> EventHandlerObjectAction
		this.associationsMap = new HashMap<ObjectTree3D, List<ObjectTree3D>>();
		this.leftTargetObjects = new ArrayList<ObjectTree3D>();
		this.leftSourceObjects = new ArrayList<ObjectTree3D>();
		this.temporalPopulation = null;
		
		//////////////////////////////////////
		this.eventListMap = new HashMap<EventType, List<Event>>();
	}

	//Handler
	public void finishObjectTracking(ObjectTree3D obj) {
		result.finishObjectTracking(obj);
	}

	public void addNewObject(ObjectTree3D obj) {
		result.addNewObject(obj);
	}

	public boolean motionFieldContains(ObjectTree3D obj1) {
		return result.getMotionField().contains(obj1);
	}

	public ObjectTree3D removeLastObject(Integer id) {
		return result.getMotionField().removeLastObject(id);
	}

	public void reconnectMissedObject(Integer id) {
		result.getMotionField().reconnectMissedObjectId(id);
	}

	@Override
	public File getRawDataDir() {
		return this.rawDataDir;
	}

	@Override
	public File getSegmentedDataDir() {
		return this.segmentedDataDir;
	}
	
}
