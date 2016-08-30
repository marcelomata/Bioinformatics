package trackingSPT.objects3D;

import java.util.ArrayList;
import java.util.List;

public class SplittedObject {
	
	private ObjectTree3D objectSource;
	private List<ObjectTree3D> targetObjects;
	private int frameEvent;
	
	public SplittedObject(ObjectTree3D source, int frame) {
		this.objectSource = source;
		this.targetObjects = new ArrayList<ObjectTree3D>();
		this.frameEvent = frame;
	}
	
	public ObjectTree3D getObjectSource() {
		return objectSource;
	}
	
	public List<ObjectTree3D> getTargetObjects() {
		return targetObjects;
	}
	
	public int getFrameEvent() {
		return frameEvent;
	}
	
	public int getId() {
		return objectSource.getId();
	}

	public void addTarget(ObjectTree3D target1) {
		this.targetObjects.add(target1);
	}

}
