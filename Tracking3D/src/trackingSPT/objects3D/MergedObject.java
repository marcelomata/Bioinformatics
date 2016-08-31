package trackingSPT.objects3D;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * A new class can be created to be used in both splitting and merging cases.
 * 
 * @author Marcelo da Mata
 *
 */
public class MergedObject {
	
	private ObjectTree3D objectTarget;
	private List<ObjectTree3D> sourceObjects;
	private int frameEvent;
	
	public MergedObject(ObjectTree3D target, int frame) {
		this.objectTarget = target;
		this.sourceObjects = new ArrayList<ObjectTree3D>();
		this.frameEvent = frame;
	}
	
	public ObjectTree3D getObjectSource() {
		return objectTarget;
	}
	
	public List<ObjectTree3D> getSourceObjects() {
		return sourceObjects;
	}
	
	public int getFrameEvent() {
		return frameEvent;
	}
	
	public int getId() {
		return objectTarget.getId();
	}

	public void addSource(ObjectTree3D target1) {
		this.sourceObjects.add(target1);
	}

}
